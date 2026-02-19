package com.zmd.auth_service.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String role) {
        super("Role " + role + " not found");
    }
}
