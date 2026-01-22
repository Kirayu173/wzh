package com.wzh.suyuan.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wzh.suyuan.backend.dto.AdminProductCreateResponse;
import com.wzh.suyuan.backend.dto.AdminProductRequest;
import com.wzh.suyuan.backend.dto.ProductDetailResponse;
import com.wzh.suyuan.backend.dto.ProductListResponse;
import com.wzh.suyuan.backend.dto.ProductSummary;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.ProductImage;
import com.wzh.suyuan.backend.model.ProductStatus;
import com.wzh.suyuan.backend.repository.ProductImageRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;
import com.wzh.suyuan.backend.util.TextUtils;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "price", "stock", "origin", "status", "createTime");

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
                        .status(product.getStatus())
                        .images(loadImages(product))
                        .build());
    }

    public ProductListResponse getAdminProducts(int page, int size, String status, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage;
        String safeStatus = normalizeStatus(status);
        String safeKeyword = TextUtils.trimToNull(keyword);
        if (safeStatus != null && safeKeyword != null) {
            productPage = productRepository.findByStatusAndNameContainingIgnoreCase(safeStatus, safeKeyword, pageable);
        } else if (safeStatus != null) {
            productPage = productRepository.findByStatus(safeStatus, pageable);
        } else if (safeKeyword != null) {
            productPage = productRepository.findByNameContainingIgnoreCase(safeKeyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
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

    public AdminProductCreateResponse createProduct(AdminProductRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request required");
        }
        Product product = Product.builder()
                .name(TextUtils.trimToNull(request.getName()))
                .price(request.getPrice())
                .stock(request.getStock())
                .origin(TextUtils.trimToNull(request.getOrigin()))
                .coverUrl(TextUtils.trimToNull(request.getCoverUrl()))
                .status(defaultStatus(request.getStatus()))
                .description(TextUtils.trimToNull(request.getDescription()))
                .createTime(LocalDateTime.now())
                .build();
        validateProduct(product);
        Product saved = productRepository.save(product);
        return new AdminProductCreateResponse(saved.getId());
    }

    public void updateProduct(Long productId, AdminProductRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request required");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        product.setName(TextUtils.trimToNull(request.getName()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setOrigin(TextUtils.trimToNull(request.getOrigin()));
        product.setCoverUrl(TextUtils.trimToNull(request.getCoverUrl()));
        product.setStatus(defaultStatus(request.getStatus()));
        product.setDescription(TextUtils.trimToNull(request.getDescription()));
        validateProduct(product);
        productRepository.save(product);
    }

    public void updateProductStatus(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        String normalized = normalizeStatus(status);
        if (normalized == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status invalid");
        }
        product.setStatus(normalized);
        productRepository.save(product);
    }

    public void updateProductStock(Long productId, Integer stock) {
        if (stock == null || stock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stock invalid");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        product.setStock(stock);
        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found");
        }
        productImageRepository.deleteByProductId(productId);
        productRepository.deleteById(productId);
    }

    private ProductSummary toSummary(Product product) {
        return ProductSummary.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .coverUrl(product.getCoverUrl())
                .origin(product.getOrigin())
                .status(product.getStatus())
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
        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            String dir = parts[1].trim().toLowerCase();
            if ("desc".equals(dir)) {
                direction = Sort.Direction.DESC;
            }
        }
        return Sort.by(direction, property);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name required");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price invalid");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stock invalid");
        }
    }

    private String normalizeStatus(String status) {
        ProductStatus normalized = ProductStatus.from(status);
        return normalized == null ? null : normalized.getValue();
    }

    private String defaultStatus(String status) {
        String normalized = normalizeStatus(status);
        return normalized == null ? ProductStatus.ONLINE.getValue() : normalized;
    }

}
