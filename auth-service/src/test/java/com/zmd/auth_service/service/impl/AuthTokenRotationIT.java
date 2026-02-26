package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.utils.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthTokenRotationIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("auth_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    void refresh_shouldRotateRefreshToken_andRevokeOldOne() throws Exception {

        // register
        var register = new RegisterRequest(
                "ezion@example.com",
                "Password1!",
                "Zion",
                "Deluyas"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // login
        var login = new LoginRequest("ezion@example.com", "Password1!");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String refreshToken1 = loginJson.get("refreshToken").asString();

        // Refresh
        var refreshRequest = new RefreshRequest(refreshToken1);

        String refreshResponse = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode refreshJson = objectMapper.readTree(refreshResponse);
        String refreshToken2 = refreshJson.get("refreshToken").asString();

        // Assert rotation
        assertThat(refreshToken2)
                .as("New refresh token should be generated and differ from old one")
                .isNotBlank()
                .isNotEqualTo(refreshToken1);

        // Assert old token is revoked in DB
        String oldHash = TokenUtils.sha256Hex(refreshToken1);

        var oldTokenOpt = refreshTokenRepository.findByTokenHash(oldHash);

        assertThat(oldTokenOpt)
                .isPresent();

        assertThat(oldTokenOpt.get().isRevoked())
                .isTrue();
    }
}