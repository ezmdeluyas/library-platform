package com.zmd.auth_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.jwt")
public record JwtConfigProperties(
        String secret,
        String issuer,
        Duration accessTtl,
        Duration refreshTtl
) {}
