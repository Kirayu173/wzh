package com.wzh.suyuan.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderBySortAsc(Long productId);

    void deleteByProductId(Long productId);
}
