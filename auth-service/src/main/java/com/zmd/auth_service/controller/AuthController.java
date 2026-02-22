package com.zmd.auth_service.controller;

import com.zmd.auth_service.api.docs.ApiCommonAuthErrors;
import com.zmd.auth_service.api.docs.ApiCommonWriteErrors;
import com.zmd.auth_service.api.docs.ApiLoginAuthErrors;
import com.zmd.auth_service.dto.request.LoginRequest;
import com.zmd.auth_service.dto.request.RefreshRequest;
import com.zmd.auth_service.dto.request.RegisterRequest;
import com.zmd.auth_service.dto.response.AuthResponse;
import com.zmd.auth_service.dto.response.MessageResponse;
import com.zmd.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "APIs for user authentication and token management")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", security = {})
    @ApiCommonWriteErrors
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        MessageResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Authenticate user and generate JWT tokens", security = {})
    @ApiCommonAuthErrors
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Refresh access token using refresh token", security = {})
    @ApiCommonWriteErrors
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }

    @Operation(summary = "Logout user and invalidate refresh token")
    @SecurityRequirement(name = "bearerAuth")
    @ApiLoginAuthErrors
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.logout(refreshRequest));
    }

}
