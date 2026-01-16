package com.wzh.suyuan.backend.controller.support;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.security.JwtUserPrincipal;

public final class AdminAuthSupport {
    private AdminAuthSupport() {
    }

    public static JwtUserPrincipal requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        String role = principal.getRole();
        if (role == null || !"admin".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return principal;
    }

    public static String maskUserId(Long userId) {
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
