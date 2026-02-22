package com.zmd.auth_service.exception;

import com.zmd.auth_service.api.error.ProblemUris;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static com.zmd.auth_service.api.error.ProblemFields.*;
import static com.zmd.auth_service.api.error.ProblemTypes.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid credentials", e.getMessage(), INVALID_CREDENTIALS, req);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRefreshToken(InvalidRefreshTokenException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid refresh token", e.getMessage(), INVALID_REFRESH_TOKEN, req);
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ProblemDetail> handleAccountDisabled(AccountDisabledException e, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Account disabled", e.getMessage(), ACCOUNT_DISABLED, req);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException e, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Email already exists", e.getMessage(), EMAIL_ALREADY_EXISTS, req);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRoleNotFound(RoleNotFoundException e, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Role not found", e.getMessage(), ROLE_NOT_FOUND, req);
    }

    @ExceptionHandler(RefreshTokenReuseDetectedException.class)
    public ResponseEntity<ProblemDetail> handleReuse(RefreshTokenReuseDetectedException e, HttpServletRequest req
    ) {
        return build(HttpStatus.UNAUTHORIZED, "Refresh token reuse detected", e.getMessage(), REFRESH_TOKEN_REUSE, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<FieldValidationError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldValidationError)
                .toList();

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid.",
                VALIDATION_ERROR,
                req
        );
        pd.setProperty(ERRORS, errors);

        return problem(HttpStatus.BAD_REQUEST, pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest req) {
        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                e.getMessage(),
                VALIDATION_ERROR,
                req
        );
        return problem(HttpStatus.BAD_REQUEST, pd);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponseException(ErrorResponseException e, HttpServletRequest req) {

        // Spring already provides a ProblemDetail body for these
        ProblemDetail pd = e.getBody();

        // Normalize instance to include query string
        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        if (pd.getInstance() == null) {
            pd.setInstance(URI.create(instance));
        }

        // Ensure timestamp exists
        if (pd.getProperties() == null || !pd.getProperties().containsKey(TIMESTAMP)) {
            pd.setProperty(TIMESTAMP, OffsetDateTime.now());
        }

        // Normalize type to your domain if missing or generic
        if (pd.getType() == null || pd.getType().toString().isBlank() || "about:blank".equals(pd.getType().toString())) {
            String slug = "http-" + e.getStatusCode().value();
            pd.setType(URI.create(ProblemUris.BASE + slug));
        }

        // Normalize title if missing
        if (pd.getTitle() == null || pd.getTitle().isBlank()) {
            pd.setTitle(e.getStatusCode().toString());
        }

        return ResponseEntity
                .status(e.getStatusCode())
                .contentType(PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                "An unexpected error occurred.",
                INTERNAL_ERROR,
                req);
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

        pd.setType(URI.create(ProblemUris.BASE + typeSlug));

        String instance = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        pd.setInstance(URI.create(instance));

        pd.setProperty(TIMESTAMP, OffsetDateTime.now());

        return pd;
    }

    private FieldValidationError toFieldValidationError(FieldError fe) {
        Object rejected = fe.getRejectedValue();
        if (rejected != null) {
            String f = fe.getField().toLowerCase();
            if (f.contains("password") || f.contains("secret") || f.contains("token")) {
                rejected = "***";
            }
        }
        return new FieldValidationError(fe.getField(), fe.getDefaultMessage(), rejected, fe.getCode());
    }

    public record FieldValidationError(String field, String message, Object rejectedValue, String code) {}
}