package com.wzh.suyuan.backend.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.ProductDetailResponse;
import com.wzh.suyuan.backend.dto.ProductListResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.model.PaginationConstants;
import com.wzh.suyuan.backend.service.ProductService;
import com.wzh.suyuan.backend.util.SecurityUtils;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) String sort,
                                                                 @RequestParam(required = false) String keyword,
                                                                 Authentication authentication) {
        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? PaginationConstants.DEFAULT_PAGE_SIZE
                : Math.min(size, PaginationConstants.MAX_PAGE_SIZE);
        String requestId = UUID.randomUUID().toString();
        log.info("products list request: requestId={}, userId={}, page={}, size={}, sort={}, keyword={}",
                requestId, SecurityUtils.maskUserId(SecurityUtils.getUserId(authentication)),
                safePage, safeSize, sort, keyword);
        ProductListResponse response = productService.getProducts(safePage, safeSize, sort, keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> detail(@PathVariable("id") Long id,
                                                                     Authentication authentication) {
        String requestId = UUID.randomUUID().toString();
        log.info("product detail request: requestId={}, userId={}, productId={}",
                requestId, SecurityUtils.maskUserId(SecurityUtils.getUserId(authentication)), id);
        return productService.getProductDetail(id)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.failure(404, "product not found")));
    }
}
