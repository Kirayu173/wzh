package com.wzh.suyuan.network.model;

public class CartUpdateRequest {
    private int quantity;

    public CartUpdateRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
