package com.zmd.auth_service.api.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemFields {
    public static final String TIMESTAMP = "timestamp";
    public static final String ERRORS = "errors";
}
