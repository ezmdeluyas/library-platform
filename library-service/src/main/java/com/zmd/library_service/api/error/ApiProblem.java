package com.zmd.library_service.api.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiProblem {

    VALIDATION_ERROR(
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            ProblemUris.BASE + ProblemTypes.VALIDATION_ERROR
    ),

    BUSINESS_RULE_VIOLATION(
            HttpStatus.CONFLICT,
            "Business rule violated",
            ProblemUris.BASE + ProblemTypes.BUSINESS_RULE_VIOLATION
    ),

    RESOURCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "Resource not found",
            ProblemUris.BASE + ProblemTypes.RESOURCE_NOT_FOUND
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "Access denied",
            ProblemUris.BASE + ProblemTypes.ACCESS_DENIED
    ),

    INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Unexpected error",
            ProblemUris.BASE + ProblemTypes.INTERNAL_ERROR
    );

    private final HttpStatus status;
    private final String title;
    private final String type;
}