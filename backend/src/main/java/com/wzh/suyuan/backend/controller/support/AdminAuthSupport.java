package com.wzh.suyuan.backend.controller.support;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.security.JwtUserPrincipal;
import com.wzh.suyuan.backend.util.SecurityUtils;

public final class AdminAuthSupport {
    private AdminAuthSupport() {
    }

    public static JwtUserPrincipal requireAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return principal;
    }

    private static boolean hasRole(Authentication authentication, String role) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return false;
        }
        for (GrantedAuthority authority : authorities) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static String maskUserId(Long userId) {
        return SecurityUtils.maskUserId(userId);
    }
}
