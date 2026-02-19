package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.config.JwtConfigProperties;
import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.dto.response.AuthResponse;
import com.zmd.auth_service.dto.response.MessageResponse;
import com.zmd.auth_service.entity.RoleEntity;
import com.zmd.auth_service.entity.UserEntity;
import com.zmd.auth_service.exception.EmailAlreadyExistsException;
import com.zmd.auth_service.exception.RoleNotFoundException;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.repository.RoleRepository;
import com.zmd.auth_service.repository.UserRepository;
import com.zmd.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String ROLE_USER = "ROLE_USER";

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

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return null;
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
