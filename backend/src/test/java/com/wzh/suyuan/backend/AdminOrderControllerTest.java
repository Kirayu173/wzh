package com.wzh.suyuan.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.wzh.suyuan.backend.entity.OrderEntity;
import com.wzh.suyuan.backend.entity.User;
import com.wzh.suyuan.backend.model.OrderStatus;
import com.wzh.suyuan.backend.repository.OrderRepository;
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
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String adminToken;
    private String userToken;
    private OrderEntity paidOrder;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin1234"));
        admin.setPhone("13900000000");
        admin.setRole("admin");
        admin.setCreateTime(LocalDateTime.now());
        admin = userRepository.save(admin);
        adminToken = tokenProvider.createToken(admin).getToken();

        User user = new User();
        user.setUsername("user");
        user.setPasswordHash(passwordEncoder.encode("user1234"));
        user.setPhone("13800000000");
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        user = userRepository.save(user);
        userToken = tokenProvider.createToken(user).getToken();

        paidOrder = OrderEntity.builder()
                .userId(user.getId())
                .totalAmount(new BigDecimal("66.00"))
                .status(OrderStatus.PAID.name())
                .payTime(LocalDateTime.now())
                .receiver("张三")
                .phone("13800000000")
                .address("北京 朝阳区 1 号")
                .createTime(LocalDateTime.now())
                .build();
        paidOrder = orderRepository.save(paidOrder);
    }

    @Test
    void adminShouldShipPaidOrder() throws Exception {
        mockMvc.perform(post("/admin/orders/" + paidOrder.getId() + "/ship")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expressNo\":\"SF123\",\"expressCompany\":\"顺丰\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(OrderStatus.SHIPPED.name()));

        OrderEntity refreshed = orderRepository.findById(paidOrder.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(OrderStatus.SHIPPED.name(), refreshed.getStatus());
    }

    @Test
    void nonAdminShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/admin/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }
}
