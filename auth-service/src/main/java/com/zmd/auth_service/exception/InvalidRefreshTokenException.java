package com.zmd.auth_service.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String refreshToken) {
        super("Invalid refresh token: " + refreshToken);
    }
}
