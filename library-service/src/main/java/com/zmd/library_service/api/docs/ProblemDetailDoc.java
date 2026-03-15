package com.zmd.library_service.api.docs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "ProblemDetail", description = "RFC 7807-style error response used by library-service APIs.")
public class ProblemDetailDoc {

    @Schema(example = "https://zmd.com/problems/validation-error")
    public String type;

    @Schema(example = "Validation failed")
    public String title;

    @Schema(example = "One or more fields are invalid.")
    public String detail;

    @Schema(example = "400")
    public Integer status;

    @Schema(example = "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4")
    public String instance;

    @Schema(example = "2026-02-22T07:25:53.051354136Z")
    public OffsetDateTime timestamp;

    @Schema(description = "Validation errors (present for validation failures).")
    public List<FieldValidationErrorDoc> errors;

    @Schema(name = "FieldValidationError")
    public static class FieldValidationErrorDoc {
        @Schema(example = "copyId")
        public String field;

        @Schema(example = "must not be null")
        public String message;

        @Schema(example = "null")
        public Object rejectedValue;

        @Schema(example = "NotNull")
        public String code;
    }
}
