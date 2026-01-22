package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wzh.suyuan.backend.model.CartConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequest {
    @NotNull(message = "quantity required")
    @Min(value = 1, message = "quantity must be >= 1")
    @Max(value = CartConstants.MAX_ITEM_QUANTITY, message = "quantity too large")
    private Integer quantity;
}
