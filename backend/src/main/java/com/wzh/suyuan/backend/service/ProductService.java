package com.wzh.suyuan.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wzh.suyuan.backend.dto.ProductDetailResponse;
import com.wzh.suyuan.backend.dto.ProductListResponse;
import com.wzh.suyuan.backend.dto.ProductSummary;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.ProductImage;
import com.wzh.suyuan.backend.repository.ProductImageRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository,
                          ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public ProductListResponse getProducts(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, parseSort(sort));
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductSummary> items = productPage.getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ProductListResponse.builder()
                .items(items)
                .page(page)
                .size(size)
                .total(productPage.getTotalElements())
                .build();
    }

    public Optional<ProductDetailResponse> getProductDetail(Long productId) {
        return productRepository.findById(productId)
                .map(product -> ProductDetailResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .coverUrl(product.getCoverUrl())
                        .origin(product.getOrigin())
                        .description(product.getDescription())
                        .images(loadImages(product))
                        .build());
    }

    private ProductSummary toSummary(Product product) {
        return ProductSummary.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .coverUrl(product.getCoverUrl())
                .origin(product.getOrigin())
                .build();
    }

    private List<String> loadImages(Product product) {
        List<String> images = new ArrayList<>();
        try {
            List<ProductImage> productImages = productImageRepository.findByProductIdOrderBySortAsc(product.getId());
            for (ProductImage image : productImages) {
                if (image.getUrl() != null && !image.getUrl().isEmpty()) {
                    images.add(image.getUrl());
                }
            }
        } catch (DataAccessException ex) {
            log.warn("product image query failed: productId={}, error={}", product.getId(), ex.getMessage());
        }
        if (images.isEmpty() && product.getCoverUrl() != null && !product.getCoverUrl().isEmpty()) {
            images.add(product.getCoverUrl());
        }
        return images;
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            String dir = parts[1].trim().toLowerCase(Locale.ROOT);
            if ("desc".equals(dir)) {
                direction = Sort.Direction.DESC;
            }
        }
        return Sort.by(direction, property);
    }
}
