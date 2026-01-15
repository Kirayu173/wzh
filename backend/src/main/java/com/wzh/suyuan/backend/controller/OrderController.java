package com.wzh.suyuan.backend.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.OrderCreateRequest;
import com.wzh.suyuan.backend.dto.OrderCreateResponse;
import com.wzh.suyuan.backend.dto.OrderDetailResponse;
import com.wzh.suyuan.backend.dto.OrderListResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> create(@Valid @RequestBody OrderCreateRequest request,
                                                                   Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order create request: requestId={}, userId={}", requestId, maskUserId(userId));
        OrderCreateResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<OrderListResponse>> list(@RequestParam(value = "status", required = false) String status,
                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                               Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order list request: requestId={}, userId={}, status={}, page={}",
                requestId, maskUserId(userId), status, page);
        OrderListResponse response = orderService.listOrders(userId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> detail(@PathVariable("id") Long id,
                                                                   Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order detail request: requestId={}, userId={}, orderId={}",
                requestId, maskUserId(userId), id);
        OrderDetailResponse response = orderService.getOrderDetail(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> pay(@PathVariable("id") Long id,
                                                                Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order pay request: requestId={}, userId={}, orderId={}",
                requestId, maskUserId(userId), id);
        OrderDetailResponse response = orderService.payOrder(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> cancel(@PathVariable("id") Long id,
                                                                   Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order cancel request: requestId={}, userId={}, orderId={}",
                requestId, maskUserId(userId), id);
        OrderDetailResponse response = orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> confirm(@PathVariable("id") Long id,
                                                                    Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("order confirm request: requestId={}, userId={}, orderId={}",
                requestId, maskUserId(userId), id);
        OrderDetailResponse response = orderService.confirmOrder(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Long getUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            return ((JwtUserPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
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
