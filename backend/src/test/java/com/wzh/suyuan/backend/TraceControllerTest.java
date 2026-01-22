package com.wzh.suyuan.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzh.suyuan.backend.entity.Product;
import com.wzh.suyuan.backend.entity.User;
import com.wzh.suyuan.backend.repository.LogisticsNodeRepository;
import com.wzh.suyuan.backend.repository.ProductRepository;
import com.wzh.suyuan.backend.repository.TraceBatchRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
class TraceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceBatchRepository traceBatchRepository;

    @Autowired
    private LogisticsNodeRepository logisticsNodeRepository;

    @Autowired
    private TestDataCleaner dataCleaner;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String adminToken;
    private String userToken;
    private Product product;

    @BeforeEach
    void setup() {
        dataCleaner.reset();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin1234"));
        admin.setPhone("13800000002");
        admin.setRole("admin");
        admin.setCreateTime(LocalDateTime.now());
        admin = userRepository.save(admin);
        adminToken = tokenProvider.createToken(admin).getToken();

        User user = new User();
        user.setUsername("user");
        user.setPasswordHash(passwordEncoder.encode("user1234"));
        user.setPhone("13800000003");
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        user = userRepository.save(user);
        userToken = tokenProvider.createToken(user).getToken();

        product = Product.builder()
                .name("溯源苹果")
                .price(new BigDecimal("12.50"))
                .stock(20)
                .coverUrl("https://example.com/apple.png")
                .origin("山东")
                .description("产地苹果")
                .status("online")
                .createTime(LocalDateTime.now())
                .build();
        product = productRepository.save(product);
    }

    @Test
    void shouldCreateTraceBatchAddLogisticsAndQueryDetail() throws Exception {
        String createResponse = mockMvc.perform(post("/admin/trace")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + product.getId()
                                + ",\"origin\":\"山东\",\"producer\":\"合作社A\",\"batchNo\":\"B001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createJson = objectMapper.readTree(createResponse);
        String traceCode = createJson.path("data").path("traceCode").asText();

        mockMvc.perform(post("/admin/trace/" + traceCode + "/logistics")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nodeTime\":\"2026-02-12T10:00:00\","
                                + "\"location\":\"仓库A\",\"statusDesc\":\"已出库\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/admin/trace/" + traceCode + "/logistics")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nodeTime\":\"2026-02-13T10:00:00\","
                                + "\"location\":\"仓库B\",\"statusDesc\":\"运输中\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/trace/" + traceCode)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.batch.traceCode").value(traceCode))
                .andExpect(jsonPath("$.data.logistics.length()").value(2))
                .andExpect(jsonPath("$.data.logistics[0].nodeTime").value("2026-02-13T10:00:00"));

        mockMvc.perform(get("/admin/trace/" + traceCode + "/qrcode")
                        .with(csrf())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void shouldReturnNotFoundForInvalidTraceCode() throws Exception {
        mockMvc.perform(get("/trace/UNKNOWN")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
