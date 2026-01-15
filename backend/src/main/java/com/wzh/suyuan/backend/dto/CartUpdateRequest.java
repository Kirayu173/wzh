package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequest {
    @NotNull(message = "quantity required")
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;
}
