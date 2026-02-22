package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "ProblemDetail", description = "RFC 7807-style error response used by this API.")
public class ProblemDetailDoc {

    @Schema(example = "https://zmd.com/problems/validation-error")
    public String type;

    @Schema(example = "Validation failed")
    public String title;

    @Schema(example = "One or more fields are invalid.")
    public String detail;

    @Schema(example = "400")
    public Integer status;

    @Schema(example = "/api/v1/auth/register")
    public String instance;

    @Schema(example = "2026-02-22T07:25:53.051354136Z")
    public OffsetDateTime timestamp;

    @Schema(description = "Validation errors (present for validation failures).")
    public List<FieldValidationErrorDoc> errors;

    @Schema(name = "FieldValidationError")
    public static class FieldValidationErrorDoc {
        @Schema(example = "email")
        public String field;

        @Schema(example = "Invalid email")
        public String message;

        @Schema(example = "not-an-email")
        public Object rejectedValue;

        @Schema(example = "Email")
        public String code;
    }
}