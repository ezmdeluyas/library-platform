package com.zmd.auth_service.exception;

public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException() {
        super("Account disabled");
    }
}
