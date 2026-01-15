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

import com.wzh.suyuan.backend.dto.OrderDetailResponse;
import com.wzh.suyuan.backend.dto.OrderListResponse;
import com.wzh.suyuan.backend.dto.OrderShipRequest;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.OrderService;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<OrderListResponse>> list(@RequestParam(required = false) String status,
                                                               @RequestParam(required = false) String keyword,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               Authentication authentication) {
        JwtUserPrincipal principal = requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? 10 : Math.min(size, 50);
        log.info("admin order list request: requestId={}, adminId={}, status={}, keyword={}, page={}, size={}",
                requestId, maskUserId(principal.getId()), status, keyword, safePage, safeSize);
        OrderListResponse response = orderService.listOrdersForAdmin(status, keyword, safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> detail(@PathVariable("id") Long id,
                                                                   Authentication authentication) {
        JwtUserPrincipal principal = requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin order detail request: requestId={}, adminId={}, orderId={}",
                requestId, maskUserId(principal.getId()), id);
        OrderDetailResponse response = orderService.getOrderDetailForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> ship(@PathVariable("id") Long id,
                                                                 @Valid @RequestBody OrderShipRequest request,
                                                                 Authentication authentication) {
        JwtUserPrincipal principal = requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin order ship request: requestId={}, adminId={}, orderId={}, expressNo={}",
                requestId, maskUserId(principal.getId()), id, request.getExpressNo());
        OrderDetailResponse response = orderService.shipOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private JwtUserPrincipal requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Unauthorized");
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        if (!isAdmin(principal)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,
                    "Forbidden");
        }
        return principal;
    }

    private boolean isAdmin(JwtUserPrincipal principal) {
        String role = principal.getRole();
        return role != null && "admin".equalsIgnoreCase(role);
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
