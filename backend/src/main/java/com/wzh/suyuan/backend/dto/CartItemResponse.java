package com.wzh.suyuan.backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Boolean selected;
    private BigDecimal priceSnapshot;
    private String productName;
    private String productImage;
}
