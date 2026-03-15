package com.zmd.library_service.controller;

import com.zmd.library_service.api.docs.ApiAccessDeniedError;
import com.zmd.library_service.api.docs.ApiCommonWriteErrors;
import com.zmd.library_service.api.docs.ApiBorrowErrors;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "APIs for borrowing, returning, and renewing book loans")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Borrow a book copy")
    @SecurityRequirement(name = "bearerAuth")
    @ApiCommonWriteErrors
    @ApiBorrowErrors
    @ApiAccessDeniedError
    @ApiResponse(responseCode = "200", description = "Book borrowed successfully")
    @PostMapping("/borrow/{copyId}")
    public ResponseEntity<LoanResponse> borrowBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.borrowBook(copyId, userId));
    }

    @Operation(summary = "Return a borrowed book copy")
    @SecurityRequirement(name = "bearerAuth")
    @ApiCommonWriteErrors
    @ApiReturnErrors
    @ApiAccessDeniedError
    @ApiResponse(responseCode = "200", description = "Book returned successfully")
    @PostMapping("/return/{copyId}")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.returnBook(copyId, userId));
    }

    @Operation(summary = "Renew an active loan")
    @SecurityRequirement(name = "bearerAuth")
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
