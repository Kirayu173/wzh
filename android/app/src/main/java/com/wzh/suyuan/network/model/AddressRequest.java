package com.wzh.suyuan.network.model;

public class AddressRequest {
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String detail;
    private Boolean isDefault;

    public AddressRequest(String receiver, String phone, String province, String city, String detail, Boolean isDefault) {
        this.receiver = receiver;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
