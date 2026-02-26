package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.entity.RefreshTokenEntity;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthLogoutSubjectOwnerMatchIT {

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
    void logout_whenAccessTokenSubjectDoesNotMatchRefreshTokenOwner_should403_andNotRevokeToken() throws Exception {
        // --- User A registers + logs in (we will use A's refresh token) ---
        String emailA = uniqueEmail();
        register(emailA);
        AuthTokens tokensA = login(emailA);

        // sanity: token exists and is not revoked yet
        String hashA = TokenUtils.sha256Hex(tokensA.refreshToken());
        RefreshTokenEntity before = refreshTokenRepository.findByTokenHash(hashA).orElseThrow();
        assertThat(before.isRevoked()).isFalse();

        // --- User B registers + logs in (we will use B's access token) ---
        String emailB = uniqueEmail();
        register(emailB);
        AuthTokens tokensB = login(emailB);

        // --- User B tries to logout using A's refresh token ---
        RefreshRequest logoutReq = new RefreshRequest(tokensA.refreshToken());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + tokensB.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutReq)))
                .andExpect(status().isForbidden()); // AccessDeniedException -> 403

        // --- Assert A's refresh token is still NOT revoked ---
        RefreshTokenEntity after = refreshTokenRepository.findByTokenHash(hashA).orElseThrow();
        assertThat(after.isRevoked())
                .as("Logout attempt by a different subject must not revoke owner's refresh token")
                .isFalse();
    }

    @Test
    void logout_withoutAuthorizationHeader_should403_andNotRevokeToken() throws Exception {
        // --- Register + login to get a real refresh token in DB ---
        String email = uniqueEmail();
        register(email);
        AuthTokens tokens = login(email);

        String hash = TokenUtils.sha256Hex(tokens.refreshToken());
        RefreshTokenEntity before = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        assertThat(before.isRevoked()).isFalse();

        // --- Call logout WITHOUT Authorization header ---
        RefreshRequest logoutReq = new RefreshRequest(tokens.refreshToken());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutReq)))
                .andExpect(status().isUnauthorized());

        // --- Assert token still not revoked ---
        RefreshTokenEntity after = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        assertThat(after.isRevoked())
                .as("Unauthenticated logout should not revoke token")
                .isFalse();
    }

    @Test
    void logout_whenSubjectMatchesOwner_shouldRevokeToken_andReturn200() throws Exception {
        // --- Register + login ---
        String email = uniqueEmail();
        register(email);
        AuthTokens tokens = login(email);

        String refreshToken = tokens.refreshToken();
        String accessToken = tokens.accessToken();
        String hash = TokenUtils.sha256Hex(refreshToken);

        // sanity check before logout
        RefreshTokenEntity before = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        assertThat(before.isRevoked()).isFalse();

        // --- Call logout with correct Authorization header ---
        RefreshRequest logoutReq = new RefreshRequest(refreshToken);

        String responseBody = mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutReq)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // --- Assert DB token is revoked ---
        RefreshTokenEntity after = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        assertThat(after.isRevoked())
                .as("Logout with correct subject should revoke refresh token")
                .isTrue();

        // --- Optional: assert response message ---
        JsonNode json = objectMapper.readTree(responseBody);
        assertThat(json.path("message").asString())
                .containsIgnoringCase("logged out");
    }

    // -------- helpers --------

    private void register(String email) throws Exception {
        var req = new RegisterRequest(email, "Password1!", "Zion", "Deluyas");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    private AuthTokens login(String email) throws Exception {
        var req = new LoginRequest(email, "Password1!");

        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        return new AuthTokens(
                json.path("accessToken").asString(),
                json.path("refreshToken").asString()
        );
    }

    private static String uniqueEmail() {
        return "ezion+" + UUID.randomUUID() + "@example.com";
    }

    private record AuthTokens(String accessToken, String refreshToken) {}
}