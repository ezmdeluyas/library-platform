package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized (refresh token invalid / reuse detected)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Invalid refresh token", value = ProblemExamples.INVALID_REFRESH_TOKEN_401),
                        @ExampleObject(name = "Refresh token reuse detected", value = ProblemExamples.REFRESH_TOKEN_REUSE_401)
                }
        )
)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden (account disabled)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Account disabled", value = ProblemExamples.ACCOUNT_DISABLED_403)
                }
        )
)
public @interface ApiRefreshErrors {}