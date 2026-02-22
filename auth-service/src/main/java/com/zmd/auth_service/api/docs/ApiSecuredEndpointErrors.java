package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized (missing/invalid access token)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Missing token", value = ProblemExamples.MISSING_TOKEN_401),
                        @ExampleObject(name = "Invalid/Expired JWT", value = ProblemExamples.INVALID_JWT_401)
                }
        )
)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class)
        )
)
public @interface ApiSecuredEndpointErrors {}