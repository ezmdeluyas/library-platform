package com.zmd.auth_service.api.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.ProblemDetail;

import java.lang.annotation.*;

import static com.zmd.auth_service.api.docs.ProblemExamples.ACCESS_DENIED_403;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "403",
        description = "Access denied",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(name = "AccessDenied", value = ACCESS_DENIED_403)
        )
)
public @interface ApiAccessDeniedError {}