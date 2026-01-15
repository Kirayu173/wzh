package com.wzh.suyuan.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.dto.OrderCreateRequest;
import com.wzh.suyuan.backend.dto.OrderCreateResponse;
import com.wzh.suyuan.backend.dto.OrderDetailResponse;
import com.wzh.suyuan.backend.dto.OrderItemResponse;
import com.wzh.suyuan.backend.dto.OrderListResponse;
import com.wzh.suyuan.backend.dto.OrderSummaryResponse;
import com.wzh.suyuan.backend.entity.Address;
import com.wzh.suyuan.backend.entity.CartItem;
import com.wzh.suyuan.backend.entity.OrderEntity;
import com.wzh.suyuan.backend.entity.OrderItem;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.model.OrderStatus;
import com.wzh.suyuan.backend.repository.AddressRepository;
import com.wzh.suyuan.backend.repository.CartItemRepository;
import com.wzh.suyuan.backend.repository.OrderItemRepository;
import com.wzh.suyuan.backend.repository.OrderRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        AddressRepository addressRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderCreateResponse createOrder(Long userId, OrderCreateRequest request) {
        long start = System.currentTimeMillis();
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items required");
        }
        String requestId = normalizeRequestId(request.getRequestId());
        if (requestId != null) {
            OrderEntity existing = orderRepository.findByUserIdAndRequestId(userId, requestId).orElse(null);
            if (existing != null) {
                log.info("order create idempotent hit: userId={}, requestId={}, orderId={}",
                        maskUserId(userId), requestId, existing.getId());
                return OrderCreateResponse.builder()
                        .id(existing.getId())
                        .status(existing.getStatus())
                        .totalAmount(existing.getTotalAmount())
                        .build();
            }
        }
        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        Map<Long, Product> productCache = new HashMap<>();
        Map<Long, Integer> reservedQuantity = new HashMap<>();
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> cartIds = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderCreateRequest.Item item : request.getItems()) {
            CartItem cartItem = cartItemRepository.findByIdAndUserId(item.getCartId(), userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cart item not found"));
            if (!cartItem.getProductId().equals(item.getProductId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "cart item mismatch");
            }
            if (Boolean.FALSE.equals(cartItem.getSelected())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "cart item not selected");
            }
            int quantity = Math.max(item.getQuantity(), 1);
            Product product = productCache.computeIfAbsent(cartItem.getProductId(), key ->
                    productRepository.findById(key)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found")));
            ensureProductAvailable(product);
            int reserved = reservedQuantity.getOrDefault(product.getId(), 0) + quantity;
            if (product.getStock() != null && product.getStock() < reserved) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "stock not enough");
            }
            reservedQuantity.put(product.getId(), reserved);
            BigDecimal price = product.getPrice();
            if (price == null) {
                price = cartItem.getPriceSnapshot() == null ? BigDecimal.ZERO : cartItem.getPriceSnapshot();
            }
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
            orderItems.add(OrderItem.builder()
                    .productId(product.getId())
                    .price(price)
                    .quantity(quantity)
                    .productName(defaultText(cartItem.getProductName(), product.getName()))
                    .productImage(defaultText(cartItem.getProductImage(), product.getCoverUrl()))
                    .build());
            cartIds.add(cartItem.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING_PAY.name())
                .receiver(address.getReceiver())
                .phone(address.getPhone())
                .address(buildAddressSnapshot(address))
                .memo(request.getMemo())
                .requestId(requestId)
                .createTime(now)
                .build();
        OrderEntity saved;
        try {
            saved = orderRepository.save(order);
        } catch (DataIntegrityViolationException ex) {
            if (requestId != null) {
                OrderEntity existing = orderRepository.findByUserIdAndRequestId(userId, requestId).orElse(null);
                if (existing != null) {
                    log.info("order create idempotent conflict: userId={}, requestId={}, orderId={}",
                            maskUserId(userId), requestId, existing.getId());
                    return OrderCreateResponse.builder()
                            .id(existing.getId())
                            .status(existing.getStatus())
                            .totalAmount(existing.getTotalAmount())
                            .build();
                }
            }
            throw ex;
        }
        for (OrderItem item : orderItems) {
            item.setOrderId(saved.getId());
        }
        orderItemRepository.saveAll(orderItems);
        updateStocks(reservedQuantity, productCache);
        if (!cartIds.isEmpty()) {
            cartItemRepository.deleteAllByIdInBatch(cartIds);
        }
        log.info("order create success: userId={}, orderId={}, amount={}, costMs={}",
                maskUserId(userId), saved.getId(), totalAmount, System.currentTimeMillis() - start);
        return OrderCreateResponse.builder()
                .id(saved.getId())
                .status(saved.getStatus())
                .totalAmount(saved.getTotalAmount())
                .build();
    }

    public OrderListResponse listOrders(Long userId, String status, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "id"));
        Page<OrderEntity> orderPage;
        if (status == null || status.isEmpty()) {
            orderPage = orderRepository.findByUserId(userId, pageable);
        } else {
            orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        }
        List<OrderEntity> orders = orderPage.getContent();
        List<OrderSummaryResponse> summaries = buildSummaries(orders);
        return OrderListResponse.builder()
                .items(summaries)
                .page(page)
                .size(size)
                .total(orderPage.getTotalElements())
                .build();
    }

    public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        return toDetailResponse(order, items);
    }

    @Transactional
    public OrderDetailResponse payOrder(Long userId, Long orderId) {
        long start = System.currentTimeMillis();
        OrderEntity order = loadOrderForUpdate(userId, orderId);
        ensureStatus(order, OrderStatus.PENDING_PAY);
        order.setStatus(OrderStatus.PAID.name());
        order.setPayTime(LocalDateTime.now());
        OrderEntity saved = orderRepository.save(order);
        List<OrderItemResponse> items = loadItemResponses(saved.getId());
        log.info("order pay success: userId={}, orderId={}, costMs={}",
                maskUserId(userId), orderId, System.currentTimeMillis() - start);
        return toDetailResponse(saved, items);
    }

    @Transactional
    public OrderDetailResponse cancelOrder(Long userId, Long orderId) {
        long start = System.currentTimeMillis();
        OrderEntity order = loadOrderForUpdate(userId, orderId);
        ensureStatus(order, OrderStatus.PENDING_PAY);
        order.setStatus(OrderStatus.CANCELED.name());
        OrderEntity saved = orderRepository.save(order);
        rollbackStock(saved.getId());
        List<OrderItemResponse> items = loadItemResponses(saved.getId());
        log.info("order cancel success: userId={}, orderId={}, costMs={}",
                maskUserId(userId), orderId, System.currentTimeMillis() - start);
        return toDetailResponse(saved, items);
    }

    @Transactional
    public OrderDetailResponse confirmOrder(Long userId, Long orderId) {
        long start = System.currentTimeMillis();
        OrderEntity order = loadOrderForUpdate(userId, orderId);
        if (!OrderStatus.SHIPPED.name().equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order status invalid");
        }
        order.setStatus(OrderStatus.COMPLETED.name());
        order.setConfirmTime(LocalDateTime.now());
        OrderEntity saved = orderRepository.save(order);
        List<OrderItemResponse> items = loadItemResponses(saved.getId());
        log.info("order confirm success: userId={}, orderId={}, costMs={}",
                maskUserId(userId), orderId, System.currentTimeMillis() - start);
        return toDetailResponse(saved, items);
    }

    private void updateStocks(Map<Long, Integer> reservedQuantity, Map<Long, Product> productCache) {
        if (reservedQuantity.isEmpty()) {
            return;
        }
        List<Product> updated = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : reservedQuantity.entrySet()) {
            Product product = productCache.get(entry.getKey());
            if (product == null) {
                continue;
            }
            if (product.getStock() != null) {
                product.setStock(product.getStock() - entry.getValue());
                updated.add(product);
            }
        }
        if (!updated.isEmpty()) {
            productRepository.saveAll(updated);
        }
    }

    private void rollbackStock(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (items.isEmpty()) {
            return;
        }
        Map<Long, Integer> restore = new HashMap<>();
        for (OrderItem item : items) {
            restore.put(item.getProductId(),
                    restore.getOrDefault(item.getProductId(), 0) + (item.getQuantity() == null ? 0 : item.getQuantity()));
        }
        List<Product> updated = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : restore.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null || product.getStock() == null) {
                continue;
            }
            product.setStock(product.getStock() + entry.getValue());
            updated.add(product);
        }
        if (!updated.isEmpty()) {
            productRepository.saveAll(updated);
        }
    }

    private void ensureProductAvailable(Product product) {
        String status = product.getStatus();
        if (status != null && !"online".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product not available");
        }
    }

    private OrderEntity loadOrderForUpdate(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
    }

    private void ensureStatus(OrderEntity order, OrderStatus expected) {
        if (!expected.name().equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order status invalid");
        }
    }

    private List<OrderSummaryResponse> buildSummaries(List<OrderEntity> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> orderIds = orders.stream().map(OrderEntity::getId).collect(Collectors.toList());
        Map<Long, List<OrderItemResponse>> itemMap = orderItemRepository.findByOrderIdIn(orderIds)
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId,
                        Collectors.mapping(this::toItemResponse, Collectors.toList())));
        return orders.stream()
                .map(order -> OrderSummaryResponse.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .totalAmount(order.getTotalAmount())
                        .createTime(order.getCreateTime())
                        .payTime(order.getPayTime())
                        .receiver(order.getReceiver())
                        .phone(order.getPhone())
                        .address(order.getAddress())
                        .items(itemMap.getOrDefault(order.getId(), new ArrayList<>()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderItemResponse> loadItemResponses(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .build();
    }

    private OrderDetailResponse toDetailResponse(OrderEntity order, List<OrderItemResponse> items) {
        return OrderDetailResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .memo(order.getMemo())
                .receiver(order.getReceiver())
                .phone(order.getPhone())
                .address(order.getAddress())
                .createTime(order.getCreateTime())
                .payTime(order.getPayTime())
                .confirmTime(order.getConfirmTime())
                .items(items)
                .build();
    }

    private String buildAddressSnapshot(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getCity());
        }
        if (address.getDetail() != null && !address.getDetail().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getDetail());
        }
        return sb.toString();
    }

    private String defaultText(String value, String fallback) {
        if (value == null || value.isEmpty()) {
            return fallback == null ? "" : fallback;
        }
        return value;
    }

    private String normalizeRequestId(String requestId) {
        if (requestId == null) {
            return null;
        }
        String trimmed = requestId.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String maskUserId(Long userId) {
        if (userId == null) {
            return "***";
        }
        String value = String.valueOf(userId);
        if (value.length() <= 2) {
            return "***" + value;
        }
        return "***" + value.substring(value.length() - 2);
    }
}
