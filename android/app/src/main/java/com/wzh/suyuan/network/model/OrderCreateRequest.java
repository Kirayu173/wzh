package com.wzh.suyuan.network.model;

import java.util.List;

public class OrderCreateRequest {
    private Long addressId;
    private String memo;
    private String requestId;
    private List<Item> items;

    public OrderCreateRequest(Long addressId, String memo, String requestId, List<Item> items) {
        this.addressId = addressId;
        this.memo = memo;
        this.requestId = requestId;
        this.items = items;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private Long cartId;
        private Long productId;
        private Integer quantity;

        public Item(Long cartId, Long productId, Integer quantity) {
            this.cartId = cartId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getCartId() {
            return cartId;
        }

        public void setCartId(Long cartId) {
            this.cartId = cartId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
