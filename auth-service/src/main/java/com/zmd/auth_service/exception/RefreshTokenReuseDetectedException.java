package com.zmd.auth_service.exception;

public class RefreshTokenReuseDetectedException extends RuntimeException {
    public RefreshTokenReuseDetectedException() {
        super("Refresh token reuse detected. All sessions revoked.");
    }
}