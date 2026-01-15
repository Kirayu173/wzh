package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderShipRequest {
    @NotBlank(message = "expressNo required")
    private String expressNo;

    @NotBlank(message = "expressCompany required")
    private String expressCompany;
}
