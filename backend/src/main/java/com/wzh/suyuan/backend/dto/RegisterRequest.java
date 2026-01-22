package com.wzh.suyuan.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username required")
    @Size(max = 64, message = "username too long")
    private String username;

    @NotBlank(message = "password required")
    @Size(min = 8, max = 128, message = "password length must be between 8 and 128")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)\\S{8,128}$",
            message = "password must contain letters and digits")
    private String password;

    @Size(max = 20, message = "phone too long")
    private String phone;
}
