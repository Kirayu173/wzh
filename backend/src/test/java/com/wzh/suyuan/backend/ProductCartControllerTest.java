package com.wzh.suyuan.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class ProductCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

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
    void shouldListAndGetProductDetail() throws Exception {
        Product product = Product.builder()
                .name("有机苹果")
                .price(new BigDecimal("12.50"))
                .stock(20)
                .coverUrl("https://example.com/apple.png")
                .origin("山东")
                .description("新鲜苹果")
                .status("online")
                .createTime(LocalDateTime.now())
                .build();
        productRepository.save(product);

        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.items[0].name").value("有机苹果"));

        mockMvc.perform(get("/products/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(product.getId()));
    }

    @Test
    void shouldAddUpdateAndDeleteCartItem() throws Exception {
        Product product = Product.builder()
                .name("有机牛奶")
                .price(new BigDecimal("9.90"))
                .stock(10)
                .coverUrl("https://example.com/milk.png")
                .origin("内蒙古")
                .description("纯牛奶")
                .status("online")
                .createTime(LocalDateTime.now())
                .build();
        productRepository.save(product);

        String addResponse = mockMvc.perform(post("/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + product.getId() + ",\"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode addJson = objectMapper.readTree(addResponse);
        long cartId = addJson.path("data").path("id").asLong();

        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].productId").value(product.getId()));

        mockMvc.perform(put("/cart/" + cartId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(2));

        mockMvc.perform(patch("/cart/" + cartId + "/select")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selected\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selected").value(false));

        mockMvc.perform(delete("/cart/" + cartId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
