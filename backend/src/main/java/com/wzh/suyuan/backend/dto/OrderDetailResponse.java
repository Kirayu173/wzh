package com.wzh.suyuan.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private String memo;
    private String receiver;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime confirmTime;
    private List<OrderItemResponse> items;
}
