package com.wzh.suyuan.backend.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "addressId required")
    private Long addressId;

    private String memo;

    private String requestId;

    @NotEmpty(message = "items required")
    private List<@Valid Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @NotNull(message = "cartId required")
        private Long cartId;

        @NotNull(message = "productId required")
        private Long productId;

        @NotNull(message = "quantity required")
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;
    }
}
