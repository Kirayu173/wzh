package com.wzh.suyuan.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrderIdIn(List<Long> orderIds);
}
