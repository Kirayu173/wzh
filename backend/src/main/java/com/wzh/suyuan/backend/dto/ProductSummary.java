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
public class ProductSummary {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String coverUrl;
    private String origin;
}
