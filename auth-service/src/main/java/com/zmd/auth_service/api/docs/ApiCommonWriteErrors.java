package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCommonAuthErrors
@ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Validation failed", value = ProblemExamples.VALIDATION_400),
                        @ExampleObject(name = "Malformed JSON", value = ProblemExamples.MALFORMED_JSON_400),
                        @ExampleObject(name = "Constraint violation", value = ProblemExamples.CONSTRAINT_VIOLATION_400)
                }
        )
)
@ApiResponse(
        responseCode = "409",
        description = "Conflict",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Email already exists", value = ProblemExamples.EMAIL_EXISTS_409)
                }
        )
)
@ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Unexpected error", value = ProblemExamples.INTERNAL_500)
                }
        )
)
public @interface ApiCommonWriteErrors {}