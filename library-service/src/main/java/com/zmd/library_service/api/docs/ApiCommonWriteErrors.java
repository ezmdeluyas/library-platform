package com.zmd.library_service.api.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiCommonAuthErrors
@ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Validation failed", value = ProblemExamples.VALIDATION_400),
                        @ExampleObject(name = "Invalid UUID", value = ProblemExamples.INVALID_UUID_400)
                }
        )
)
@ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Unexpected error", value = ProblemExamples.INTERNAL_500)
                }
        )
)
public @interface ApiCommonWriteErrors {
}
