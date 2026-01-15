package com.wzh.suyuan.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.dto.CartAddResponse;
import com.wzh.suyuan.backend.dto.CartItemResponse;
import com.wzh.suyuan.backend.entity.CartItem;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.repository.CartItemRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;

@Service
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        return cartItemRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CartAddResponse addItem(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        int safeQuantity = Math.max(quantity, 1);
        int targetQuantity = safeQuantity;
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (item != null) {
            targetQuantity = item.getQuantity() + safeQuantity;
        }
        ensureStock(product, targetQuantity, userId, productId);
        LocalDateTime now = LocalDateTime.now();
        if (item == null) {
            item = CartItem.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(targetQuantity)
                    .selected(true)
                    .priceSnapshot(product.getPrice())
                    .productName(product.getName())
                    .productImage(product.getCoverUrl())
                    .createTime(now)
                    .updateTime(now)
                    .build();
        } else {
            item.setQuantity(targetQuantity);
            item.setPriceSnapshot(product.getPrice());
            item.setProductName(product.getName());
            item.setProductImage(product.getCoverUrl());
            item.setUpdateTime(now);
        }
        CartItem saved = cartItemRepository.save(item);
        log.info("cart add success: userId={}, productId={}, cartId={}",
                maskUserId(userId), productId, saved.getId());
        return CartAddResponse.builder().id(saved.getId()).build();
    }

    public CartItemResponse updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cart item not found"));
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        int safeQuantity = Math.max(quantity, 1);
        ensureStock(product, safeQuantity, userId, item.getProductId());
        item.setQuantity(safeQuantity);
        item.setPriceSnapshot(product.getPrice());
        item.setProductName(product.getName());
        item.setProductImage(product.getCoverUrl());
        item.setUpdateTime(LocalDateTime.now());
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    public CartItemResponse updateSelected(Long userId, Long cartItemId, boolean selected) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cart item not found"));
        item.setSelected(selected);
        item.setUpdateTime(LocalDateTime.now());
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    public void deleteItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cart item not found"));
        cartItemRepository.delete(item);
        log.info("cart delete: userId={}, cartId={}", maskUserId(userId), cartItemId);
    }

    private void ensureStock(Product product, int targetQuantity, Long userId, Long productId) {
        if (product.getStock() != null && product.getStock() < targetQuantity) {
            log.warn("stock not enough: userId={}, productId={}, quantity={}, stock={}",
                    maskUserId(userId), productId, targetQuantity, product.getStock());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "stock not enough");
        }
    }

    private CartItemResponse toResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .selected(item.getSelected())
                .priceSnapshot(item.getPriceSnapshot())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .build();
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
