package com.zmd.auth_service.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        Instant expiresAt,

        UUID userId,

        String email,

        Set<String> roles
) {}
