package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Missing token", value = ProblemExamples.MISSING_TOKEN_401),
                        @ExampleObject(name = "Invalid credentials", value = ProblemExamples.INVALID_CREDENTIALS_401),
                        @ExampleObject(name = "Invalid refresh token", value = ProblemExamples.INVALID_REFRESH_TOKEN_401)
                }
        )
)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Account disabled", value = ProblemExamples.ACCOUNT_DISABLED_403)
                }
        )
)
public @interface ApiCommonAuthErrors {}