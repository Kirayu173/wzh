package com.wzh.suyuan.data.product;

import com.wzh.suyuan.data.db.entity.ProductEntity;
import com.wzh.suyuan.network.model.Product;

public final class ProductMapper {
    private ProductMapper() {
    }

    public static ProductEntity toEntity(Product product) {
        if (product == null || product.getId() == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setCoverUrl(product.getCoverUrl());
        entity.setOrigin(product.getOrigin());
        entity.setDescription(product.getDescription());
        entity.setUpdatedAt(System.currentTimeMillis());
        return entity;
    }

    public static Product toModel(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setCoverUrl(entity.getCoverUrl());
        product.setOrigin(entity.getOrigin());
        product.setDescription(entity.getDescription());
        return product;
    }
}
