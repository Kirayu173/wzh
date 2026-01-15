package com.wzh.suyuan.backend.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 64)
    private String receiver;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 32)
    private String province;

    @Column(length = 32)
    private String city;

    @Column(length = 255)
    private String detail;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
