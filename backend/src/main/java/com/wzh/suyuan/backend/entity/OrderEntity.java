package com.wzh.suyuan.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "request_id"})
})
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 32, nullable = false)
    private String status;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "confirm_time")
    private LocalDateTime confirmTime;

    @Column(name = "ship_time")
    private LocalDateTime shipTime;

    @Column(name = "express_no", length = 64)
    private String expressNo;

    @Column(name = "express_company", length = 64)
    private String expressCompany;

    @Column(length = 64)
    private String receiver;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String memo;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
