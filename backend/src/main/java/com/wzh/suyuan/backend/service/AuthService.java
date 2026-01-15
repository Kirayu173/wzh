package com.wzh.suyuan.backend.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wzh.suyuan.backend.dto.AuthUserResponse;
import com.wzh.suyuan.backend.dto.LoginResponse;
import com.wzh.suyuan.backend.dto.RegisterResponse;
import com.wzh.suyuan.backend.entity.User;
import com.wzh.suyuan.backend.exception.InvalidCredentialsException;
import com.wzh.suyuan.backend.exception.UserAlreadyExistsException;
import com.wzh.suyuan.backend.repository.UserRepository;
import com.wzh.suyuan.backend.security.JwtTokenProvider;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public RegisterResponse register(String username, String password, String phone, String requestId) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("register failed: requestId={}, username={}", requestId, maskUsername(username));
            throw new UserAlreadyExistsException("user already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        User saved = userRepository.save(user);
        log.info("register success: requestId={}, userId={}", requestId, maskUserId(saved.getId()));
        return RegisterResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .build();
    }

    public LoginResponse login(String username, String password, String requestId) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn("login failed: requestId={}, username={}", requestId, maskUsername(username));
            throw new InvalidCredentialsException("invalid credentials");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("login failed: requestId={}, userId={}", requestId, maskUserId(user.getId()));
            throw new InvalidCredentialsException("invalid credentials");
        }
        JwtTokenProvider.JwtToken jwtToken = tokenProvider.createToken(user);
        log.info("login success: requestId={}, userId={}, expireAt={}", requestId, maskUserId(user.getId()), jwtToken.getExpireAt());
        return LoginResponse.builder()
                .token(jwtToken.getToken())
                .expireAt(jwtToken.getExpireAt())
                .user(toAuthUser(user))
                .build();
    }

    public AuthUserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));
        return toAuthUser(user);
    }

    private AuthUserResponse toAuthUser(User user) {
        return AuthUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
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

    private String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "***";
        }
        if (username.length() <= 2) {
            return username.charAt(0) + "*";
        }
        return username.substring(0, 2) + "***";
    }
}
