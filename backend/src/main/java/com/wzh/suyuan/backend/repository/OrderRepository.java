package com.wzh.suyuan.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

    Page<OrderEntity> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);

    Optional<OrderEntity> findByUserIdAndRequestId(Long userId, String requestId);
}
