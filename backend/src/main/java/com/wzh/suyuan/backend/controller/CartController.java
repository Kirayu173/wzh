package com.wzh.suyuan.backend.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.CartAddRequest;
import com.wzh.suyuan.backend.dto.CartAddResponse;
import com.wzh.suyuan.backend.dto.CartItemResponse;
import com.wzh.suyuan.backend.dto.CartSelectRequest;
import com.wzh.suyuan.backend.dto.CartUpdateRequest;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> list(Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("cart list request: requestId={}, userId={}", requestId, maskUserId(userId));
        List<CartItemResponse> items = cartService.getCartItems(userId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartAddResponse>> add(@Valid @RequestBody CartAddRequest request,
                                                           Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("cart add request: requestId={}, userId={}, productId={}, quantity={}",
                requestId, maskUserId(userId), request.getProductId(), request.getQuantity());
        CartAddResponse response = cartService.addItem(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(@PathVariable("id") Long id,
                                                                        @Valid @RequestBody CartUpdateRequest request,
                                                                        Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("cart update request: requestId={}, userId={}, cartId={}, quantity={}",
                requestId, maskUserId(userId), id, request.getQuantity());
        CartItemResponse response = cartService.updateQuantity(userId, id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/select")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateSelected(@PathVariable("id") Long id,
                                                                        @Valid @RequestBody CartSelectRequest request,
                                                                        Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("cart select request: requestId={}, userId={}, cartId={}, selected={}",
                requestId, maskUserId(userId), id, request.getSelected());
        CartItemResponse response = cartService.updateSelected(userId, id, request.getSelected());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id,
                                                      Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("cart delete request: requestId={}, userId={}, cartId={}",
                requestId, maskUserId(userId), id);
        cartService.deleteItem(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
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
