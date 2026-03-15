package com.zmd.library_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.zmd.library_service.api.error.ProblemFields.ERRORS;
import static com.zmd.library_service.api.error.ProblemFields.TIMESTAMP;
import static com.zmd.library_service.api.error.ProblemTypes.*;
import static com.zmd.library_service.api.error.ProblemUris.BASE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolationException(
            BusinessRuleViolationException e,
            HttpServletRequest req
    ) {
        String detail = messageOrDefault(e.getMessage(), "The request violates a business rule.");
        return build(
                HttpStatus.CONFLICT,
                "Business rule violated",
                detail,
                BUSINESS_RULE_VIOLATION,
                req
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException e,
            HttpServletRequest req
    ) {
        String detail = messageOrDefault(e.getMessage(), "The requested resource was not found.");
        return build(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                detail,
                RESOURCE_NOT_FOUND,
                req
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest req
    ) {
        List<FieldValidationError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldValidationError::toFieldValidationError)
                .toList();

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED_TITLE,
                "One or more fields are invalid.",
                VALIDATION_ERROR,
                req
        );
        pd.setProperty(ERRORS, errors);

        return problem(HttpStatus.BAD_REQUEST, pd);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest req
    ) {
        String detail = "Invalid value for parameter: " + e.getName();

        if (e.getRequiredType() != null && UUID.class.equals(e.getRequiredType())) {
            detail = "Invalid UUID format for parameter: " + e.getName();
        }

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED_TITLE,
                detail,
                VALIDATION_ERROR,
                req
        );

        return problem(HttpStatus.BAD_REQUEST, pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest req
    ) {
        List<FieldValidationError> errors = e.getConstraintViolations()
                .stream()
                .map(FieldValidationError::toFieldValidationError)
                .toList();

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                VALIDATION_FAILED_TITLE,
                "One or more values are invalid.",
                VALIDATION_ERROR,
                req
        );
        pd.setProperty(ERRORS, errors);

        return problem(HttpStatus.BAD_REQUEST, pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest req
    ) {
        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");

        String type = BASE + ACCESS_DENIED;

        log.warn("event=access_denied status=403 type={} instance={}", type, instance);

        return build(
                HttpStatus.FORBIDDEN,
                "Access denied",
                "You do not have permission to access this resource.",
                ACCESS_DENIED,
                req
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e, HttpServletRequest req) {
        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        String type = BASE + INTERNAL_ERROR;

        log.error("event=unhandled_exception status=500 type={} instance={}", type, instance, e);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                "An unexpected error occurred.",
                INTERNAL_ERROR,
                req
        );
    }

    private ResponseEntity<ProblemDetail> build(
            HttpStatus status,
            String title,
            String detail,
            String typeSlug,
            HttpServletRequest req
    ) {
        ProblemDetail pd = baseProblem(status, title, detail, typeSlug, req);
        return problem(status, pd);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, ProblemDetail pd) {
        return ResponseEntity
                .status(status)
                .contentType(PROBLEM_JSON)
                .body(pd);
    }

    private ProblemDetail baseProblem(
            HttpStatus status,
            String title,
            String detail,
            String typeSlug,
            HttpServletRequest req
    ) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(BASE + typeSlug));

        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        pd.setInstance(URI.create(instance));

        pd.setProperty(TIMESTAMP, OffsetDateTime.now());
        return pd;
    }

    private String messageOrDefault(String message, String fallback) {
        return message != null && !message.isBlank() ? message : fallback;
    }
}
