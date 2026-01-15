package com.wzh.suyuan.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.dto.AddressRequest;
import com.wzh.suyuan.backend.dto.AddressResponse;
import com.wzh.suyuan.backend.entity.Address;
import com.wzh.suyuan.backend.repository.AddressRepository;

@Service
public class AddressService {
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressResponse> list(Long userId) {
        return addressRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        boolean setDefault = Boolean.TRUE.equals(request.getIsDefault());
        if (!setDefault && addressRepository.countByUserId(userId) == 0) {
            setDefault = true;
        }
        if (setDefault) {
            clearDefault(userId, null);
        }
        Address address = Address.builder()
                .userId(userId)
                .receiver(request.getReceiver())
                .phone(request.getPhone())
                .province(request.getProvince())
                .city(request.getCity())
                .detail(request.getDetail())
                .isDefault(setDefault)
                .createTime(LocalDateTime.now())
                .build();
        Address saved = addressRepository.save(address);
        log.info("address create: userId={}, addressId={}, default={}",
                maskUserId(userId), saved.getId(), saved.getIsDefault());
        return toResponse(saved);
    }

    @Transactional
    public AddressResponse update(Long userId, Long id, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefault(userId, id);
            address.setIsDefault(true);
        } else if (request.getIsDefault() != null) {
            address.setIsDefault(false);
        }
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDetail(request.getDetail());
        Address saved = addressRepository.save(address);
        log.info("address update: userId={}, addressId={}, default={}",
                maskUserId(userId), saved.getId(), saved.getIsDefault());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        addressRepository.delete(address);
        log.info("address delete: userId={}, addressId={}", maskUserId(userId), id);
    }

    @Transactional
    public AddressResponse setDefault(Long userId, Long id) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));
        clearDefault(userId, id);
        address.setIsDefault(true);
        Address saved = addressRepository.save(address);
        log.info("address set default: userId={}, addressId={}", maskUserId(userId), id);
        return toResponse(saved);
    }

    private void clearDefault(Long userId, Long keepId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        for (Address item : defaults) {
            if (keepId == null || !keepId.equals(item.getId())) {
                item.setIsDefault(false);
            }
        }
        if (!defaults.isEmpty()) {
            addressRepository.saveAll(defaults);
        }
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .receiver(address.getReceiver())
                .phone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
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
}
