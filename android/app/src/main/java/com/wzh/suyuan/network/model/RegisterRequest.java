package com.wzh.suyuan.network.model;

public class RegisterRequest {
    private String username;
    private String password;
    private String phone;

    public RegisterRequest(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }
}
