package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.config.JwtConfigProperties;
import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.dto.response.AuthResponse;
import com.zmd.auth_service.dto.response.MessageResponse;
import com.zmd.auth_service.entity.RefreshTokenEntity;
import com.zmd.auth_service.entity.RoleEntity;
import com.zmd.auth_service.entity.UserEntity;
import com.zmd.auth_service.exception.AccountDisabledException;
import com.zmd.auth_service.exception.EmailAlreadyExistsException;
import com.zmd.auth_service.exception.InvalidCredentialsException;
import com.zmd.auth_service.exception.RoleNotFoundException;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.repository.RoleRepository;
import com.zmd.auth_service.repository.UserRepository;
import com.zmd.auth_service.service.AuthService;
import com.zmd.auth_service.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String ROLE_USER = "ROLE_USER";
    private static final String TOKEN_TYPE = "Bearer";
    private final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtConfigProperties jwtConfigProperties;

    @Transactional
    @Override
    public MessageResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.email().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        RoleEntity userRole = roleRepository.findByName(ROLE_USER).orElseThrow(() -> new RoleNotFoundException(ROLE_USER));
        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        UserEntity user = UserEntity.createNew(
                UUID.randomUUID(),
                email,
                registerRequest.firstName().trim(),
                registerRequest.lastName().trim(),
                encodedPassword,
                Set.of(userRole)
        );

        userRepository.save(user);
        return new MessageResponse("User registered successfully");
    }

    @Transactional
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email().toLowerCase().trim();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        Instant now = Instant.now();
        Instant accessExpiry = now.plus(jwtConfigProperties.accessTtl());
        Instant refreshExpiry = now.plus(jwtConfigProperties.refreshTtl());

        Set<String> roleNames = user.getRoles().stream().map(RoleEntity::getName).collect(toSet());

        JwtClaimsSet claims =JwtClaimsSet.builder()
                .issuer(jwtConfigProperties.issuer())
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(accessExpiry)
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .build();

        Jwt jwt = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(() -> "HS256").build(),
                        claims
                ));

        String token = jwt.getTokenValue();
        String refreshToken = TokenUtils.generateRefreshTokenHex(32);
        String tokenHash = TokenUtils.sha256Hex(refreshToken);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.createNew(
                UUID.randomUUID(),
                user,
                tokenHash,
                refreshExpiry
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(token, refreshToken, TOKEN_TYPE, accessExpiry, user.getId(), user.getEmail(), roleNames);
    }

    @Override
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        return null;
    }

    @Override
    public MessageResponse logout(RefreshRequest refreshRequest) {
        return null;
    }
}
