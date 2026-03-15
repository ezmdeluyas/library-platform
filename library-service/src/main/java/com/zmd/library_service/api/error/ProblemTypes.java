package com.zmd.library_service.api.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemTypes {
    public static final String BUSINESS_RULE_VIOLATION = "business-rule-violation";
    public static final String RESOURCE_NOT_FOUND = "resource-not-found";
    public static final String ACCESS_DENIED = "access-denied";
    public static final String INTERNAL_ERROR = "internal-error";
    public static final String VALIDATION_ERROR = "validation-error";
    public static final String UNAUTHORIZED = "unauthorized";
}
