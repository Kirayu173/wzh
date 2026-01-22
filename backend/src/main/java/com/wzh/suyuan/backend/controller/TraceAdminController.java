package com.wzh.suyuan.backend.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wzh.suyuan.backend.controller.support.AdminAuthSupport;

import com.wzh.suyuan.backend.dto.TraceBatchCreateRequest;
import com.wzh.suyuan.backend.dto.TraceBatchCreateResponse;
import com.wzh.suyuan.backend.dto.TraceBatchListResponse;
import com.wzh.suyuan.backend.dto.TraceBatchResponse;
import com.wzh.suyuan.backend.dto.TraceLogisticsCreateResponse;
import com.wzh.suyuan.backend.dto.TraceLogisticsRequest;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.model.PaginationConstants;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.TraceService;

@RestController
@RequestMapping("/admin/trace")
public class TraceAdminController {
    private static final Logger log = LoggerFactory.getLogger(TraceAdminController.class);

    private final TraceService traceService;

    public TraceAdminController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TraceBatchListResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false) String keyword,
                                                                    Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? PaginationConstants.DEFAULT_PAGE_SIZE
                : Math.min(size, PaginationConstants.MAX_PAGE_SIZE);
        log.info("trace batch list request: requestId={}, adminId={}, page={}, size={}, keyword={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), safePage, safeSize, keyword);
        TraceBatchListResponse response = traceService.listBatches(safePage, safeSize, keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TraceBatchCreateResponse>> createBatch(@Valid @RequestBody TraceBatchCreateRequest request,
                                                                             Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("trace batch create request: requestId={}, adminId={}, productId={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), request.getProductId());
        TraceBatchCreateResponse response = traceService.createBatch(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{traceCode}/logistics")
    public ResponseEntity<ApiResponse<TraceLogisticsCreateResponse>> addLogistics(@PathVariable("traceCode") String traceCode,
                                                                                  @Valid @RequestBody TraceLogisticsRequest request,
                                                                                  Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("trace logistics create request: requestId={}, adminId={}, traceCode={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), traceCode);
        TraceLogisticsCreateResponse response = traceService.addLogisticsNode(traceCode, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(value = "/{traceCode}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable("traceCode") String traceCode,
                                            Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("trace qrcode request: requestId={}, adminId={}, traceCode={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), traceCode);
        byte[] payload = traceService.generateQrCode(traceCode);
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TraceBatchResponse>> updateBatch(@PathVariable("id") Long id,
                                                                       @Valid @RequestBody TraceBatchCreateRequest request,
                                                                       Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("trace batch update request: requestId={}, adminId={}, batchId={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id);
        TraceBatchResponse response = traceService.updateBatch(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBatch(@PathVariable("id") Long id,
                                                           Authentication authentication) {
        JwtUserPrincipal principal = AdminAuthSupport.requireAdmin(authentication);
        String requestId = UUID.randomUUID().toString();
        log.info("trace batch delete request: requestId={}, adminId={}, batchId={}",
                requestId, AdminAuthSupport.maskUserId(principal.getId()), id);
        traceService.deleteBatch(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
