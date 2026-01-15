package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @NotBlank(message = "receiver required")
    private String receiver;

    @NotBlank(message = "phone required")
    private String phone;

    private String province;

    private String city;

    @NotBlank(message = "detail required")
    private String detail;

    private Boolean isDefault;
}
