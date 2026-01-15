package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username required")
    @Size(max = 64, message = "username too long")
    private String username;

    @NotBlank(message = "password required")
    @Size(max = 128, message = "password too long")
    private String password;

    @Size(max = 20, message = "phone too long")
    private String phone;
}
