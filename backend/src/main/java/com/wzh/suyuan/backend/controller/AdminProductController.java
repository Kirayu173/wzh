package com.wzh.suyuan.backend.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.AdminProductCreateResponse;
import com.wzh.suyuan.backend.dto.AdminProductRequest;
import com.wzh.suyuan.backend.dto.AdminProductStatusRequest;
import com.wzh.suyuan.backend.dto.AdminProductStockRequest;
import com.wzh.suyuan.backend.dto.ProductListResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.model.PaginationConstants;
import com.wzh.suyuan.backend.controller.support.AdminAuthSupport;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.ProductService;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {
    private static final Logger log = LoggerFactory.getLogger(AdminProductController.class);

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(required = false) String keyword,
                                                                 Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? PaginationConstants.DEFAULT_PAGE_SIZE
                : Math.min(size, PaginationConstants.MAX_PAGE_SIZE);
        log.info("admin product list request: requestId={}, adminId={}, page={}, size={}, status={}, keyword={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), safePage, safeSize, status, keyword);
        ProductListResponse response = productService.getAdminProducts(safePage, safeSize, status, keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminProductCreateResponse>> create(@Valid @RequestBody AdminProductRequest request,
                                                                          Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin product create request: requestId={}, adminId={}, name={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), request.getName());
        AdminProductCreateResponse response = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable("id") Long id,
                                                      @Valid @RequestBody AdminProductRequest request,
                                                      Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin product update request: requestId={}, adminId={}, productId={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id);
        productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(@PathVariable("id") Long id,
                                                            @Valid @RequestBody AdminProductStatusRequest request,
                                                            Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin product status request: requestId={}, adminId={}, productId={}, status={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id, request.getStatus());
        productService.updateProductStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Object>> updateStock(@PathVariable("id") Long id,
                                                           @Valid @RequestBody AdminProductStockRequest request,
                                                           Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin product stock request: requestId={}, adminId={}, productId={}, stock={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id, request.getStock());
        productService.updateProductStock(id, request.getStock());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id,
                                                      Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("admin product delete request: requestId={}, adminId={}, productId={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
