package com.zmd.library_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.jwt")
public record JwtConfigProperties(
        String secret,
        String issuer
) {}
