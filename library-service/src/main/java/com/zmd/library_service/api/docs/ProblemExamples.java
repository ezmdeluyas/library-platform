package com.zmd.library_service.api.docs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemExamples {

    public static final String VALIDATION_400 = """
        {
          "type": "https://zmd.com/problems/validation-error",
          "title": "Validation failed",
          "status": 400,
          "detail": "One or more values are invalid.",
          "instance": "/api/v1/loans/borrow/not-a-uuid",
          "timestamp": "2026-02-22T07:31:22.987654321Z"
        }
        """;

    public static final String INVALID_UUID_400 = """
        {
          "type": "https://zmd.com/problems/validation-error",
          "title": "Validation failed",
          "status": 400,
          "detail": "Invalid UUID format for parameter: copyId",
          "instance": "/api/v1/loans/borrow/not-a-uuid",
          "timestamp": "2026-02-22T07:31:22.987654321Z"
        }
        """;

    public static final String UNAUTHORIZED_401 = """
        {
          "type": "https://zmd.com/problems/unauthorized",
          "title": "Unauthorized",
          "status": 401,
          "detail": "Authentication is required to access this resource.",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:26:18.560763896Z"
        }
        """;

    public static final String ACCESS_DENIED_403 = """
        {
          "type": "https://zmd.com/problems/access-denied",
          "title": "Access denied",
          "status": 403,
          "detail": "You do not have permission to access this resource.",
          "instance": "/api/v1/loans/renew/1498f44f-d472-45ad-bfcb-fd63203ef7f8",
          "timestamp": "2026-02-26T07:25:53.051354136Z"
        }
        """;

    public static final String BORROW_COPY_NOT_FOUND_404 = """
        {
          "type": "https://zmd.com/problems/resource-not-found",
          "title": "Resource not found",
          "status": 404,
          "detail": "Book copy not found",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:40:00.000000000Z"
        }
        """;

    public static final String RETURN_ACTIVE_LOAN_NOT_FOUND_404 = """
        {
          "type": "https://zmd.com/problems/resource-not-found",
          "title": "Resource not found",
          "status": 404,
          "detail": "Active loan not found for this copy",
          "instance": "/api/v1/loans/return/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:40:30.000000000Z"
        }
        """;

    public static final String RENEW_ACTIVE_LOAN_NOT_FOUND_404 = """
        {
          "type": "https://zmd.com/problems/resource-not-found",
          "title": "Resource not found",
          "status": 404,
          "detail": "Active loan not found",
          "instance": "/api/v1/loans/renew/1498f44f-d472-45ad-bfcb-fd63203ef7f8",
          "timestamp": "2026-02-22T07:40:45.000000000Z"
        }
        """;

    public static final String BORROW_BOOK_NOT_FOUND_404 = """
        {
          "type": "https://zmd.com/problems/resource-not-found",
          "title": "Resource not found",
          "status": 404,
          "detail": "Book not found",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:40:15.000000000Z"
        }
        """;

    public static final String BORROW_COPY_NOT_AVAILABLE_409 = """
        {
          "type": "https://zmd.com/problems/business-rule-violation",
          "title": "Business rule violated",
          "status": 409,
          "detail": "Copy is not available",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:41:00.000000000Z"
        }
        """;

    public static final String BORROW_MAX_ACTIVE_LOANS_409 = """
        {
          "type": "https://zmd.com/problems/business-rule-violation",
          "title": "Business rule violated",
          "status": 409,
          "detail": "Maximum number of active loans reached",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:41:30.000000000Z"
        }
        """;

    public static final String RENEW_OVERDUE_LOAN_409 = """
        {
          "type": "https://zmd.com/problems/business-rule-violation",
          "title": "Business rule violated",
          "status": 409,
          "detail": "Overdue loans cannot be renewed",
          "instance": "/api/v1/loans/renew/1498f44f-d472-45ad-bfcb-fd63203ef7f8",
          "timestamp": "2026-02-22T07:42:00.000000000Z"
        }
        """;

    public static final String RENEW_MAX_RENEWALS_409 = """
        {
          "type": "https://zmd.com/problems/business-rule-violation",
          "title": "Business rule violated",
          "status": 409,
          "detail": "Loan has reached the maximum number of renewals",
          "instance": "/api/v1/loans/renew/1498f44f-d472-45ad-bfcb-fd63203ef7f8",
          "timestamp": "2026-02-22T07:42:30.000000000Z"
        }
        """;

    public static final String BORROW_COPY_ALREADY_BORROWED_409 = """
        {
          "type": "https://zmd.com/problems/business-rule-violation",
          "title": "Business rule violated",
          "status": 409,
          "detail": "Copy is already borrowed",
          "instance": "/api/v1/loans/borrow/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:41:45.000000000Z"
        }
        """;

    public static final String INTERNAL_500 = """
        {
          "type": "https://zmd.com/problems/internal-error",
          "title": "Unexpected error",
          "status": 500,
          "detail": "An unexpected error occurred.",
          "instance": "/api/v1/loans/return/8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4",
          "timestamp": "2026-02-22T07:45:00.000000000Z"
        }
        """;
}
