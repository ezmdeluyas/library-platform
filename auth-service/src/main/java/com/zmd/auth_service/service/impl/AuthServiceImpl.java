package com.zmd.auth_service.service.impl;

import com.zmd.auth_service.config.JwtConfigProperties;
import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.dto.response.AuthResponse;
import com.zmd.auth_service.dto.response.MessageResponse;
import com.zmd.auth_service.repository.RefreshTokenRepository;
import com.zmd.auth_service.repository.RoleRepository;
import com.zmd.auth_service.repository.UserRepository;
import com.zmd.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtConfigProperties jwtConfigProperties;


    @Override
    public MessageResponse register(RegisterRequest registerRequest) {
        return null;
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
