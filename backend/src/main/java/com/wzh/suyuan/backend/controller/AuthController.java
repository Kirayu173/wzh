package com.wzh.suyuan.backend.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.AuthUserResponse;
import com.wzh.suyuan.backend.dto.LoginRequest;
import com.wzh.suyuan.backend.dto.LoginResponse;
import com.wzh.suyuan.backend.dto.RegisterRequest;
import com.wzh.suyuan.backend.dto.RegisterResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.info("register request: requestId={}", requestId);
        RegisterResponse response = authService.register(request.getUsername(), request.getPassword(), request.getPhone(), requestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.info("login request: requestId={}", requestId);
        LoginResponse response = authService.login(request.getUsername(), request.getPassword(), requestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        AuthUserResponse response = authService.getUserInfo(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
