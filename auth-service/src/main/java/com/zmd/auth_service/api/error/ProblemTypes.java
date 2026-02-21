package com.zmd.auth_service.api.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemTypes {
    public static final String INVALID_CREDENTIALS = "invalid-credentials";
    public static final String INVALID_REFRESH_TOKEN = "invalid-refresh-token";
    public static final String ACCOUNT_DISABLED = "account-disabled";
    public static final String EMAIL_ALREADY_EXISTS = "email-already-exists";
    public static final String ROLE_NOT_FOUND = "role-not-found";
    public static final String VALIDATION_ERROR = "validation-error";
    public static final String INTERNAL_ERROR = "internal-error";
    public static final String UNAUTHORIZED = "unauthorized";
    public static final String ACCESS_DENIED = "access-denied";
}
