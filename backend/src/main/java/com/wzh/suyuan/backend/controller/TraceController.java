package com.wzh.suyuan.backend.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.TraceDetailResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.TraceService;

@RestController
@RequestMapping("/trace")
public class TraceController {
    private static final Logger log = LoggerFactory.getLogger(TraceController.class);

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/{traceCode}")
    public ResponseEntity<ApiResponse<TraceDetailResponse>> getTrace(@PathVariable("traceCode") String traceCode,
                                                                     Authentication authentication) {
        String requestId = UUID.randomUUID().toString();
        log.info("trace detail request: requestId={}, userId={}, traceCode={}",
                requestId, maskUserId(getUserId(authentication)), traceCode);
        TraceDetailResponse response = traceService.getTraceDetail(traceCode);
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
