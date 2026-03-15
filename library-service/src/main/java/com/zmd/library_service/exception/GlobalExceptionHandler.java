package com.zmd.library_service.exception;

import com.zmd.library_service.api.error.ApiProblem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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
        return build(ApiProblem.BUSINESS_RULE_VIOLATION, detail, req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException e,
            HttpServletRequest req
    ) {
        String detail = messageOrDefault(e.getMessage(), "The requested resource was not found.");
        return build(ApiProblem.RESOURCE_NOT_FOUND, detail, req);
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
                ApiProblem.VALIDATION_ERROR,
                "One or more fields are invalid.",
                req
        );
        pd.setProperty(ERRORS, errors);

        return problem(ApiProblem.VALIDATION_ERROR.getStatus(), pd);
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
                ApiProblem.VALIDATION_ERROR,
                detail,
                req
        );

        return problem(ApiProblem.VALIDATION_ERROR.getStatus(), pd);
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
                ApiProblem.VALIDATION_ERROR,
                "One or more values are invalid.",
                req
        );
        pd.setProperty(ERRORS, errors);

        return problem(ApiProblem.VALIDATION_ERROR.getStatus(), pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest req
    ) {
        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        String type = ApiProblem.ACCESS_DENIED.getType();

        log.warn("event=access_denied status=403 type={} instance={}", type, instance);

        return build(
                ApiProblem.ACCESS_DENIED,
                "You do not have permission to access this resource.",
                req
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e, HttpServletRequest req) {
        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        String type = ApiProblem.INTERNAL_ERROR.getType();

        log.error("event=unhandled_exception status=500 type={} instance={}", type, instance, e);

        return build(
                ApiProblem.INTERNAL_ERROR,
                "An unexpected error occurred.",
                req
        );
    }

    private ResponseEntity<ProblemDetail> build(
            ApiProblem apiProblem,
            String detail,
            HttpServletRequest req
    ) {
        ProblemDetail pd = baseProblem(apiProblem, detail, req);
        return problem(apiProblem.getStatus(), pd);
    }

    private ResponseEntity<ProblemDetail> problem(org.springframework.http.HttpStatus status, ProblemDetail pd) {
        return ResponseEntity
                .status(status)
                .contentType(PROBLEM_JSON)
                .body(pd);
    }

    private ProblemDetail baseProblem(
            ApiProblem apiProblem,
            String detail,
            HttpServletRequest req
    ) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(apiProblem.getStatus(), detail);
        pd.setTitle(apiProblem.getTitle());
        pd.setType(URI.create(apiProblem.getType()));

        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        pd.setInstance(URI.create(instance));

        pd.setProperty(TIMESTAMP, OffsetDateTime.now());
        return pd;
    }

    private String messageOrDefault(String message, String fallback) {
        return message != null && !message.isBlank() ? message : fallback;
    }
}