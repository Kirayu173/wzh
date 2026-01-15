package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductStockRequest {
    @NotNull(message = "stock required")
    @Min(value = 0, message = "stock must be >= 0")
    private Integer stock;
}
