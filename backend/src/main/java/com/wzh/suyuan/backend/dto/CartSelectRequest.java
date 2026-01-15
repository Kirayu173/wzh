package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSelectRequest {
    @NotNull(message = "selected required")
    private Boolean selected;
}
