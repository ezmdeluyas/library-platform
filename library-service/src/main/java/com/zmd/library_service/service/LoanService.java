package com.zmd.library_service.service;

import com.zmd.library_service.dto.response.LoanResponse;

import java.util.UUID;

public interface LoanService {
    LoanResponse borrowBook(UUID copyId, UUID userId);
    LoanResponse returnBook(UUID copyId, UUID userId);
    LoanResponse renewLoan(UUID loanId, UUID userId);
}
