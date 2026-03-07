package com.zmd.library_service.dto.response;

import com.zmd.library_service.entity.LoanEntity;

import java.time.Instant;
import java.util.UUID;

public record LoanResponse(
        UUID loanId,
        UUID bookCopyId,
        UUID userId,
        Instant borrowedAt,
        Instant dueAt,
        Instant returnedAt,
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
