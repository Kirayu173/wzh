package com.wzh.suyuan.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wzh.suyuan.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long expirationSeconds;

    public JwtTokenProvider(@Value("${security.jwt.secret}") String secret,
                            @Value("${security.jwt.expiration-seconds:7200}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public JwtToken createToken(User user) {
        long now = System.currentTimeMillis();
        long expireAt = now + expirationSeconds * 1000;
        Date nowDate = new Date(now);
        Date expireDate = new Date(expireAt);
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return new JwtToken(token, expireAt);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public static class JwtToken {
        private final String token;
        private final long expireAt;

        public JwtToken(String token, long expireAt) {
            this.token = token;
            this.expireAt = expireAt;
        }

        public String getToken() {
            return token;
        }

        public long getExpireAt() {
            return expireAt;
        }
    }
}
