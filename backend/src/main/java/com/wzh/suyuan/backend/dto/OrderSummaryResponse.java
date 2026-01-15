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
public class OrderSummaryResponse {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private String receiver;
    private String phone;
    private String address;
    private List<OrderItemResponse> items;
}
