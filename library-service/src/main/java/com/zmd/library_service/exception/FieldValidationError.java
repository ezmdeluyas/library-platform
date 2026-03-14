package com.zmd.library_service.exception;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

public record FieldValidationError(
        String field,
        String message,
        Object rejectedValue,
        String code
) {
    public static FieldValidationError toFieldValidationError(FieldError fe) {
        Object rejected = fe.getRejectedValue();
        return new FieldValidationError(fe.getField(), fe.getDefaultMessage(), rejected, fe.getCode());
    }

    public static FieldValidationError toFieldValidationError(ConstraintViolation<?> violation) {
        return new FieldValidationError(
                extractField(violation),
                violation.getMessage(),
                violation.getInvalidValue(),
                violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .getSimpleName()
        );
    }

    private static String extractField(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath() != null
                ? violation.getPropertyPath().toString()
                : "";

        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}