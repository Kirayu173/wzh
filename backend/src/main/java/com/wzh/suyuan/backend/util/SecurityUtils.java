package com.wzh.suyuan.backend.util;

import org.springframework.security.core.Authentication;

import com.wzh.suyuan.backend.security.JwtUserPrincipal;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static Long getUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            return ((JwtUserPrincipal) authentication.getPrincipal()).getId();
        }
        return null;
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
