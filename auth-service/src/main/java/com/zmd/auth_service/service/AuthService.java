package com.zmd.auth_service.service;

import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.dto.response.AuthResponse;
import com.zmd.auth_service.dto.response.MessageResponse;

public interface AuthService {
    MessageResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse refresh(RefreshRequest refreshRequest);

    MessageResponse logout(RefreshRequest refreshRequest);
}
