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

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = @ExampleObject(name = "Book copy not found", value = ProblemExamples.BORROW_COPY_NOT_FOUND_404)
        )
)
@ApiResponse(
        responseCode = "409",
        description = "Business rule violation",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetailDoc.class),
                examples = {
                        @ExampleObject(name = "Copy not available", value = ProblemExamples.BORROW_COPY_NOT_AVAILABLE_409),
                        @ExampleObject(name = "Max active loans reached", value = ProblemExamples.BORROW_MAX_ACTIVE_LOANS_409)
                }
        )
)
public @interface ApiBorrowErrors {
}
