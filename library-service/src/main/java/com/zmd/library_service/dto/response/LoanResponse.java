package com.zmd.library_service.dto.response;

import com.zmd.library_service.entity.LoanEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Loan operation response")
public record LoanResponse(

        @Schema(description = "Loan identifier", example = "1498f44f-d472-45ad-bfcb-fd63203ef7f8")
        UUID loanId,

        @Schema(description = "Borrowed book copy identifier", example = "8b4f78f7-0330-4f04-a3ea-2ef6e2b3f5b4")
        UUID bookCopyId,

        @Schema(description = "Borrowing user identifier", example = "cf5ddf5d-df4f-4f4f-93ef-6b9be0d30201")
        UUID userId,

        @Schema(description = "Timestamp when the book was borrowed", example = "2026-03-15T10:00:00Z")
        Instant borrowedAt,

        @Schema(description = "Timestamp when the loan is due", example = "2026-03-29T10:00:00Z")
        Instant dueAt,

        @Schema(description = "Timestamp when the loan was returned, null if still active", nullable = true, example = "2026-03-20T08:30:00Z")
        Instant returnedAt,

        @Schema(description = "Number of renewals already used", example = "1")
        short renewalCount
) {
    public static LoanResponse from(LoanEntity loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBookCopy().getId(),
                loan.getUserId(),
                loan.getBorrowedAt(),
                loan.getDueAt(),
                loan.getReturnedAt(),
                loan.getRenewalCount()
        );
    }
}