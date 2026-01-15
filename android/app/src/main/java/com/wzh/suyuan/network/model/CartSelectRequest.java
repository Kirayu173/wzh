package com.wzh.suyuan.network.model;

public class CartSelectRequest {
    private boolean selected;

    public CartSelectRequest(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
