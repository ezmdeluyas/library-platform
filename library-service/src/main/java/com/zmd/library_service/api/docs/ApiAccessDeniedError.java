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
@ApiResponse(
        responseCode = "403",
        description = "Access denied",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = @ExampleObject(name = "Access denied", value = ProblemExamples.ACCESS_DENIED_403)
        )
)
public @interface ApiAccessDeniedError {
}
