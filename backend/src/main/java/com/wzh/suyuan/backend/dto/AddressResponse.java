package com.wzh.suyuan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String detail;
    private Boolean isDefault;
}
