package com.wzh.suyuan.backend.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductRequest {
    @NotBlank(message = "name required")
    private String name;

    @NotNull(message = "price required")
    @DecimalMin(value = "0.00", inclusive = true, message = "price must be >= 0")
    private BigDecimal price;

    @NotNull(message = "stock required")
    private Integer stock;

    private String origin;

    private String coverUrl;

    private String status;

    private String description;
}
