package com.wzh.suyuan.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
