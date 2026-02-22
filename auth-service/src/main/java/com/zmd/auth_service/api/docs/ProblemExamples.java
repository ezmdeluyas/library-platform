package com.zmd.auth_service.api.docs;

public final class ProblemExamples {
    private ProblemExamples() {}

    // ----- 400 -----
    public static final String VALIDATION_400 = """
        {
          "detail": "One or more fields are invalid.",
          "instance": "/api/v1/auth/register",
          "status": 400,
          "title": "Validation failed",
          "type": "https://zmd.com/problems/validation-error",
          "timestamp": "2026-02-22T07:25:53.051354136Z",
          "errors": [
            { "field": "firstName", "message": "First name is required", "rejectedValue": "", "code": "NotBlank" },
            { "field": "lastName", "message": "Last name is required", "rejectedValue": "", "code": "NotBlank" },
            { "field": "password", "message": "Password must be between 8 and 16 characters long", "rejectedValue": "***", "code": "Size" },
            { "field": "password", "message": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character", "rejectedValue": "***", "code": "Pattern" },
            { "field": "email", "message": "Invalid email", "rejectedValue": "not-an-email", "code": "Email" }
          ]
        }
        """;

    public static final String MALFORMED_JSON_400 = """
        {
          "detail": "Malformed JSON request.",
          "instance": "/api/v1/auth/register",
          "status": 400,
          "title": "Bad Request",
          "type": "https://zmd.com/problems/http-400",
          "timestamp": "2026-02-22T07:30:10.123456789Z"
        }
        """;

    public static final String CONSTRAINT_VIOLATION_400 = """
        {
          "detail": "registerRequest.email: Invalid email",
          "instance": "/api/v1/auth/register",
          "status": 400,
          "title": "Validation failed",
          "type": "https://zmd.com/problems/validation-error",
          "timestamp": "2026-02-22T07:31:22.987654321Z"
        }
        """;

    // ----- 401 -----
    public static final String MISSING_TOKEN_401 = """
        {
          "detail": "Authentication is required to access this resource.",
          "instance": "/api/v1/auth/logout",
          "status": 401,
          "title": "Unauthorized",
          "type": "https://zmd.com/problems/unauthorized",
          "timestamp": "2026-02-22T07:26:18.560763896Z"
        }
        """;

    public static final String INVALID_CREDENTIALS_401 = """
        {
          "detail": "Invalid username or password.",
          "instance": "/api/v1/auth/login",
          "status": 401,
          "title": "Invalid credentials",
          "type": "https://zmd.com/problems/invalid-credentials",
          "timestamp": "2026-02-22T07:33:11.111111111Z"
        }
        """;

    public static final String INVALID_REFRESH_TOKEN_401 = """
        {
          "detail": "Refresh token is invalid or expired.",
          "instance": "/api/v1/auth/refresh",
          "status": 401,
          "title": "Invalid refresh token",
          "type": "https://zmd.com/problems/invalid-refresh-token",
          "timestamp": "2026-02-22T07:34:22.222222222Z"
        }
        """;

    // ----- 403 -----
    public static final String ACCOUNT_DISABLED_403 = """
        {
          "detail": "Account is disabled.",
          "instance": "/api/v1/auth/login",
          "status": 403,
          "title": "Account disabled",
          "type": "https://zmd.com/problems/account-disabled",
          "timestamp": "2026-02-22T07:35:33.333333333Z"
        }
        """;

    // ----- 409 -----
    public static final String EMAIL_EXISTS_409 = """
        {
          "detail": "Email already exists: ezmdeluyas@example.com",
          "instance": "/api/v1/auth/register",
          "status": 409,
          "title": "Email already exists",
          "type": "https://zmd.com/problems/email-already-exists",
          "timestamp": "2026-02-22T07:26:46.068884818Z"
        }
        """;

    // ----- 500 (optional but elite) -----
    public static final String INTERNAL_500 = """
        {
          "detail": "An unexpected error occurred.",
          "instance": "/api/v1/auth/register",
          "status": 500,
          "title": "Unexpected error",
          "type": "https://zmd.com/problems/internal-error",
          "timestamp": "2026-02-22T07:40:00.000000000Z"
        }
        """;
}