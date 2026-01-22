package com.wzh.suyuan.backend;

import org.springframework.stereotype.Component;

import com.wzh.suyuan.backend.repository.AddressRepository;
import com.wzh.suyuan.backend.repository.CartItemRepository;
import com.wzh.suyuan.backend.repository.LogisticsNodeRepository;
import com.wzh.suyuan.backend.repository.OrderItemRepository;
import com.wzh.suyuan.backend.repository.OrderRepository;
import com.wzh.suyuan.backend.repository.ProductImageRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;
import com.wzh.suyuan.backend.repository.TraceBatchRepository;
import com.wzh.suyuan.backend.repository.UserRepository;

@Component
public class TestDataCleaner {
    private final LogisticsNodeRepository logisticsNodeRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductImageRepository productImageRepository;
    private final TraceBatchRepository traceBatchRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public TestDataCleaner(LogisticsNodeRepository logisticsNodeRepository,
                           OrderItemRepository orderItemRepository,
                           CartItemRepository cartItemRepository,
                           AddressRepository addressRepository,
                           ProductImageRepository productImageRepository,
                           TraceBatchRepository traceBatchRepository,
                           OrderRepository orderRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.logisticsNodeRepository = logisticsNodeRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productImageRepository = productImageRepository;
        this.traceBatchRepository = traceBatchRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void reset() {
        logisticsNodeRepository.deleteAll();
        orderItemRepository.deleteAll();
        cartItemRepository.deleteAll();
        addressRepository.deleteAll();
        productImageRepository.deleteAll();
        traceBatchRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
}
