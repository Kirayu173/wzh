package com.wzh.suyuan.data.cart;

import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.Product;

public final class CartMapper {
    private CartMapper() {
    }

    public static CartEntity toEntity(long userId, CartItem item) {
        if (item == null || item.getId() == null) {
            return null;
        }
        CartEntity entity = new CartEntity();
        entity.setId(item.getId());
        entity.setUserId(userId);
        entity.setProductId(item.getProductId() == null ? 0L : item.getProductId());
        entity.setQuantity(item.getQuantity() == null ? 1 : item.getQuantity());
        entity.setSelected(item.getSelected() == null || item.getSelected());
        entity.setPriceSnapshot(item.getPriceSnapshot());
        entity.setProductName(item.getProductName());
        entity.setProductImage(item.getProductImage());
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.setSynced(true);
        return entity;
    }

    public static CartEntity toLocalEntity(long userId, Product product, int quantity) {
        CartEntity entity = new CartEntity();
        entity.setId(-System.currentTimeMillis());
        entity.setUserId(userId);
        entity.setProductId(product != null && product.getId() != null ? product.getId() : 0L);
        entity.setQuantity(Math.max(quantity, 1));
        entity.setSelected(true);
        entity.setPriceSnapshot(product != null ? product.getPrice() : null);
        entity.setProductName(product != null ? product.getName() : "");
        entity.setProductImage(product != null ? product.getCoverUrl() : "");
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.setSynced(false);
        return entity;
    }
}
