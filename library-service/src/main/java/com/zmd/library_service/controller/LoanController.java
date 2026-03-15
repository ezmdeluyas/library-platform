package com.zmd.library_service.controller;

import com.zmd.library_service.api.docs.ApiAccessDeniedError;
import com.zmd.library_service.api.docs.ApiBorrowErrors;
import com.zmd.library_service.api.docs.ApiCommonWriteErrors;
import com.zmd.library_service.api.docs.ApiRenewErrors;
import com.zmd.library_service.api.docs.ApiReturnErrors;
import com.zmd.library_service.dto.response.LoanResponse;
import com.zmd.library_service.service.LoanService;
import com.zmd.library_service.util.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "APIs for borrowing, returning, and renewing book loans")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    @Operation(
            summary = "Borrow a book copy",
            description = "Allows an authenticated user to borrow an available copy. Borrowing fails if the copy or its parent book is not found, the copy is unavailable, or the user has reached the maximum number of active loans."
    )
    @ApiCommonWriteErrors
    @ApiBorrowErrors
    @ApiAccessDeniedError
    @ApiResponse(responseCode = "200", description = "Book borrowed successfully")
    @PostMapping("/borrow/{copyId}")
    public ResponseEntity<LoanResponse> borrowBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.borrowBook(copyId, userId));
    }

    @Operation(
            summary = "Return a borrowed book copy",
            description = "Returns the authenticated user's active loan for the specified copy. Fails when no active loan exists for that user and copy."
    )
    @ApiCommonWriteErrors
    @ApiReturnErrors
    @ApiAccessDeniedError
    @ApiResponse(responseCode = "200", description = "Book returned successfully")
    @PostMapping("/return/{copyId}")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.returnBook(copyId, userId));
    }

    @Operation(
            summary = "Renew an active loan",
            description = "Extends the due date of the authenticated user's active loan. Renewal is not allowed for overdue loans or loans that already reached the maximum number of renewals."
    )
    @ApiCommonWriteErrors
    @ApiRenewErrors
    @ApiAccessDeniedError
    @ApiResponse(responseCode = "200", description = "Loan renewed successfully")
    @PostMapping("/renew/{loanId}")
    public ResponseEntity<LoanResponse> renewLoan(@PathVariable UUID loanId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.renewLoan(loanId, userId));
    }
}