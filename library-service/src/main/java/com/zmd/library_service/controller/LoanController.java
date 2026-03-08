package com.zmd.library_service.controller;

import com.zmd.library_service.dto.response.LoanResponse;
import com.zmd.library_service.service.LoanService;
import com.zmd.library_service.util.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/borrow/{copyId}")
    public ResponseEntity<LoanResponse> borrowBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.borrowBook(copyId, userId));
    }

    @PostMapping("/return/{copyId}")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable UUID copyId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.returnBook(copyId, userId));
    }

    @PostMapping("/renew/{loanId}")
    public ResponseEntity<LoanResponse> renewLoan(@PathVariable UUID loanId, Authentication authentication) {
        UUID userId = JwtUtility.extractUserId(authentication);
        return ResponseEntity.ok(loanService.renewLoan(loanId, userId));
    }
}