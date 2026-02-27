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
import com.zmd.auth_service.exception.*;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.repository.RoleRepository;
import com.zmd.auth_service.repository.UserRepository;
import com.zmd.auth_service.service.AuthService;
import com.zmd.auth_service.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String ROLE_USER = "ROLE_USER";
    private static final String TOKEN_TYPE = "Bearer";
    private static final String LOGGED_OUT_MESSAGE = "Logged out successfully";
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

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(() -> "HS256").build(),
                        claims
                )).getTokenValue();

        String refreshToken = TokenUtils.generateRefreshTokenHex(32);
        String tokenHash = TokenUtils.sha256Hex(refreshToken);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.createNew(
                UUID.randomUUID(),
                user,
                tokenHash,
                refreshExpiry
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken, TOKEN_TYPE, accessExpiry, user.getId(), user.getEmail(), roleNames);
    }

    @Transactional(noRollbackFor = RefreshTokenReuseDetectedException.class)
    @Override
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        String raw = refreshRequest.refreshToken().trim();
        String hash = TokenUtils.sha256Hex(raw);

        RefreshTokenEntity existing = refreshTokenRepository
                .findByTokenHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);

        Instant now = Instant.now();

        if (existing.isExpired(now)) {
            throw new InvalidRefreshTokenException();
        }

        UserEntity user = existing.getUser();

        if (existing.isRevoked()) {

            // If it was revoked because it was rotated → reuse attack
            if (existing.getReplacedByTokenId() != null) {
                refreshTokenRepository.revokeAllActiveByUserId(
                        existing.getUser().getId(),
                        now
                );
                refreshTokenRepository.flush();
                throw new RefreshTokenReuseDetectedException();
            }

            // revoked for other reasons (logout, manual revoke, etc.)
            throw new InvalidRefreshTokenException();
        }

        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }

        // Prepare new token FIRST
        String newRaw = TokenUtils.generateRefreshTokenHex(32);
        String newHash = TokenUtils.sha256Hex(newRaw);
        UUID newId = UUID.randomUUID();
        Instant refreshExpiry = now.plus(jwtConfigProperties.refreshTtl());

        RefreshTokenEntity replacement = RefreshTokenEntity.createNew(
                newId,
                user,
                newHash,
                refreshExpiry
        );

        // Must save & flush replacement first due to FK on replaced_by_token_id.
        // Ensures UPDATE rotate() does not violate FK under concurrency.
        refreshTokenRepository.save(replacement);
        refreshTokenRepository.flush();

        // Atomic rotation
        int updated = refreshTokenRepository.rotate(hash, newId, now);

        if (updated == 0) {
            // Someone else already rotated it → reuse attack
            refreshTokenRepository.revokeAllActiveByUserId(user.getId(), now);
            refreshTokenRepository.flush();
            throw new RefreshTokenReuseDetectedException();
        }

        // Generate new access token
        Set<String> roleNames = user.getRoles()
                .stream()
                .map(RoleEntity::getName)
                .collect(toSet());

        Instant accessExpiry = now.plus(jwtConfigProperties.accessTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtConfigProperties.issuer())
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(accessExpiry)
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(() -> "HS256").build(),
                        claims
                )
        ).getTokenValue();

        return new AuthResponse(accessToken, newRaw, TOKEN_TYPE, accessExpiry, user.getId(), user.getEmail(), roleNames);
    }

    @Transactional
    @Override
    public MessageResponse logout(RefreshRequest refreshRequest) {
        String raw = refreshRequest.refreshToken().trim();
        String hash = TokenUtils.sha256Hex(raw);

        RefreshTokenEntity existing = refreshTokenRepository.findByTokenHash(hash).orElse(null);
        Instant now = Instant.now();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Jwt jwt) {
            subject = jwt.getSubject();
        }

        if (existing == null) {
            return new MessageResponse(LOGGED_OUT_MESSAGE);
        }

        if (subject == null) {
            throw new AccessDeniedException("Logout not allowed: unauthenticated");
        }

        String ownerId = existing.getUser().getId().toString();
        if (!subject.equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (existing.isRevoked() || existing.isExpired(now)) {
            return new MessageResponse(LOGGED_OUT_MESSAGE);
        }

        existing.revoke(now);

        return new MessageResponse(LOGGED_OUT_MESSAGE);
    }
}
