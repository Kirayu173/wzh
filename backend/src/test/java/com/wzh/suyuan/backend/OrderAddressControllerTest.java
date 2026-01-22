package com.wzh.suyuan.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzh.suyuan.backend.entity.OrderEntity;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.User;
import com.wzh.suyuan.backend.model.OrderStatus;
import com.wzh.suyuan.backend.repository.AddressRepository;
import com.wzh.suyuan.backend.repository.CartItemRepository;
import com.wzh.suyuan.backend.repository.OrderItemRepository;
import com.wzh.suyuan.backend.repository.OrderRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;
import com.wzh.suyuan.backend.repository.UserRepository;
import com.wzh.suyuan.backend.security.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret=test_secret_key_32_chars_minimum_123456"
})
class OrderAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestDataCleaner dataCleaner;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String token;

    @BeforeEach
    void setup() {
        dataCleaner.reset();
        User user = new User();
        user.setUsername("tester");
        user.setPasswordHash(passwordEncoder.encode("pass1234"));
        user.setPhone("13800000001");
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        user = userRepository.save(user);
        token = tokenProvider.createToken(user).getToken();
    }

    @Test
    void shouldMaintainSingleDefaultAddress() throws Exception {
        mockMvc.perform(post("/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiver\":\"张三\",\"phone\":\"13800000001\",\"province\":\"北京\",\"city\":\"北京\","
                                + "\"detail\":\"朝阳路1号\",\"isDefault\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiver\":\"李四\",\"phone\":\"13800000002\",\"province\":\"上海\",\"city\":\"上海\","
                                + "\"detail\":\"徐汇路9号\",\"isDefault\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/addresses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].isDefault").value(true))
                .andExpect(jsonPath("$.data[1].isDefault").value(false));
    }

    @Test
    void shouldCreatePayCancelAndConfirmOrder() throws Exception {
        Product product = Product.builder()
                .name("有机橙子")
                .price(new BigDecimal("15.00"))
                .stock(10)
                .coverUrl("https://example.com/orange.png")
                .origin("湖南")
                .description("新鲜橙子")
                .status("online")
                .createTime(LocalDateTime.now())
                .build();
        productRepository.save(product);

        String addressResponse = mockMvc.perform(post("/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiver\":\"王五\",\"phone\":\"13800000003\",\"province\":\"广东\",\"city\":\"深圳\","
                                + "\"detail\":\"福田路8号\",\"isDefault\":true}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long addressId = objectMapper.readTree(addressResponse).path("data").path("id").asLong();

        String cartResponse = mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + product.getId() + ",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long cartId = objectMapper.readTree(cartResponse).path("data").path("id").asLong();

        String requestId = "req-" + System.currentTimeMillis();
        String orderResponse = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"addressId\":" + addressId + ",\"memo\":\"尽快送达\","
                                + "\"requestId\":\"" + requestId + "\","
                                + "\"items\":[{\"cartId\":" + cartId + ",\"productId\":" + product.getId()
                                + ",\"quantity\":2}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(OrderStatus.PENDING_PAY.name()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long orderId = objectMapper.readTree(orderResponse).path("data").path("id").asLong();

        String retryResponse = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"addressId\":" + addressId + ",\"memo\":\"尽快送达\","
                                + "\"requestId\":\"" + requestId + "\","
                                + "\"items\":[{\"cartId\":" + cartId + ",\"productId\":" + product.getId()
                                + ",\"quantity\":2}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long retryOrderId = objectMapper.readTree(retryResponse).path("data").path("id").asLong();
        org.junit.jupiter.api.Assertions.assertEquals(orderId, retryOrderId);

        Product refreshed = productRepository.findById(product.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(8, refreshed.getStock());

        mockMvc.perform(post("/orders/" + orderId + "/pay")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(OrderStatus.PAID.name()));

        mockMvc.perform(post("/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));

        mockMvc.perform(post("/orders/" + orderId + "/confirm")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));

        String cartResponse2 = mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + product.getId() + ",\"quantity\":1}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long cartId2 = objectMapper.readTree(cartResponse2).path("data").path("id").asLong();

        String orderResponse2 = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"addressId\":" + addressId + ","
                                + "\"items\":[{\"cartId\":" + cartId2 + ",\"productId\":" + product.getId()
                                + ",\"quantity\":1}]}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long orderId2 = objectMapper.readTree(orderResponse2).path("data").path("id").asLong();

        mockMvc.perform(post("/orders/" + orderId2 + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(OrderStatus.CANCELED.name()));

        OrderEntity order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.SHIPPED.name());
        orderRepository.save(order);

        mockMvc.perform(post("/orders/" + orderId + "/confirm")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(OrderStatus.COMPLETED.name()));
    }
}
