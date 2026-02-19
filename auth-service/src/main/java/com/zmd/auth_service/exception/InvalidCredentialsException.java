package com.zmd.auth_service.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("Invalid credentials"); }
}
