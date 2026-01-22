package com.wzh.suyuan.backend.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.wzh.suyuan.backend.dto.AddressRequest;
import com.wzh.suyuan.backend.dto.AddressResponse;
import com.wzh.suyuan.backend.model.ApiResponse;
import com.wzh.suyuan.backend.service.AddressService;
import com.wzh.suyuan.backend.util.SecurityUtils;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> list(Authentication authentication) {
        Long userId = SecurityUtils.getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("address list request: requestId={}, userId={}", requestId, SecurityUtils.maskUserId(userId));
        List<AddressResponse> data = addressService.list(userId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@Valid @RequestBody AddressRequest request,
                                                              Authentication authentication) {
        Long userId = SecurityUtils.getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("address create request: requestId={}, userId={}", requestId, SecurityUtils.maskUserId(userId));
        AddressResponse response = addressService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(@PathVariable("id") Long id,
                                                              @Valid @RequestBody AddressRequest request,
                                                              Authentication authentication) {
        Long userId = SecurityUtils.getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("address update request: requestId={}, userId={}, addressId={}",
                requestId, SecurityUtils.maskUserId(userId), id);
        AddressResponse response = addressService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id,
                                                      Authentication authentication) {
        Long userId = SecurityUtils.getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("address delete request: requestId={}, userId={}, addressId={}",
                requestId, SecurityUtils.maskUserId(userId), id);
        addressService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(@PathVariable("id") Long id,
                                                                   Authentication authentication) {
        Long userId = SecurityUtils.getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "Unauthorized"));
        }
        String requestId = UUID.randomUUID().toString();
        log.info("address default request: requestId={}, userId={}, addressId={}",
                requestId, SecurityUtils.maskUserId(userId), id);
        AddressResponse response = addressService.setDefault(userId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
