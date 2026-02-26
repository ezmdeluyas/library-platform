package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.utils.TokenUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void cleanup() {
        jdbc.execute("TRUNCATE TABLE refresh_tokens RESTART IDENTITY CASCADE");
        jdbc.execute("TRUNCATE TABLE user_roles RESTART IDENTITY CASCADE");
        jdbc.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void refresh_shouldRotateRefreshToken_andRevokeOldOne() throws Exception {

        // register
        String email = "ezion+" + java.util.UUID.randomUUID() + "@example.com";
        var register = new RegisterRequest(
                email,
                "Password1!",
                "Zion",
                "Deluyas"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // login
        var login = new LoginRequest(email, "Password1!");

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

    @Test
    void refresh_reuseOldRefreshToken_should401_andRevokeAllSessions() throws Exception {

        // register
        String email = "ezion+" + java.util.UUID.randomUUID() + "@example.com";
        var register = new RegisterRequest(
                email,
                "Password1!",
                "Zion",
                "Deluyas"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // login
        var login = new LoginRequest(email, "Password1!");

        String loginBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginNode = objectMapper.readTree(loginBody);
        String refreshToken1 = loginNode.path("refreshToken").asString();

        assertThat(refreshToken1).isNotBlank();

        // Refresh using refreshToken1 -> refreshToken2 (rotation)
        var refreshReq1 = new RefreshRequest(refreshToken1);

        String refreshBody1 = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode refreshNode1 = objectMapper.readTree(refreshBody1);
        String refreshToken2 = refreshNode1.path("refreshToken").asString();

        assertThat(refreshToken2)
                .isNotBlank()
                .isNotEqualTo(refreshToken1);

        // Reuse old refreshToken1 again -> MUST be 401 (reuse detected)
        String reuseBody = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq1)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Optional: assert problem type if you return it
        // (adjust if your slug differs)
        JsonNode reuseNode = objectMapper.readTree(reuseBody);
        assertThat(reuseNode.path("type").asString())
                .contains("refresh-token-reuse");

        // After reuse detection, even refreshToken2 should now be unusable -> 401
        var refreshReq2 = new RefreshRequest(refreshToken2);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq2)))
                .andExpect(status().isUnauthorized());

        // After reuse detection, assert ALL tokens for this user are revoked in DB
        String userId = loginNode.path("userId").asString();

        Integer activeCount = jdbc.queryForObject(
        """
            select count(*) 
            from refresh_tokens 
            where user_id = cast(? as uuid)
            and revoked_at is null
        """, Integer.class, userId);

        assertThat(activeCount)
                .as("After reuse detected, there should be NO active refresh tokens left for the user")
                .isZero();
    }

}