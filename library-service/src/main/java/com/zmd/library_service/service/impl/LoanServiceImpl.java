package com.zmd.library_service.service.impl;

import com.zmd.library_service.config.LibraryLoanConfig;
import com.zmd.library_service.dto.response.LoanResponse;
import com.zmd.library_service.entity.BookCopyEntity;
import com.zmd.library_service.entity.LoanEntity;
import com.zmd.library_service.exception.BusinessRuleViolationException;
import com.zmd.library_service.exception.ResourceNotFoundException;
import com.zmd.library_service.repository.BookCopyRepository;
import com.zmd.library_service.repository.LoanRepository;
import com.zmd.library_service.service.LoanService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LibraryLoanConfig loanConfig;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public LoanResponse borrowBook(UUID copyId, UUID userId) {
        BookCopyEntity copy = bookCopyRepository
                .findByIdAndDeletedAtIsNull(copyId)
                .orElseThrow(ResourceNotFoundException::new);

        if (copy.getBook().getDeletedAt() != null) {
            throw new ResourceNotFoundException();
        }

        if (!copy.isAvailable()) {
            throw new BusinessRuleViolationException("Copy is not available");
        }

        long activeLoanCount = loanRepository.countByUserIdAndReturnedAtIsNull(userId);
        if (activeLoanCount >= loanConfig.maxActive()) {
            throw new BusinessRuleViolationException("Maximum active loans reached");
        }

        Instant dueAt = Instant.now().plus(Duration.ofDays(loanConfig.defaultDays()));

        copy.markBorrowed();

        LoanEntity loan = LoanEntity.createNew(
                UUID.randomUUID(),
                copy,
                userId,
                dueAt
        );

        try {
            return saveRefreshAndMap(loan);
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            throw new BusinessRuleViolationException("Copy is already borrowed");
        }
    }

    @Override
    @Transactional
    public LoanResponse returnBook(UUID copyId, UUID userId) {
        LoanEntity loan = loanRepository
                .findByBookCopyIdAndUserIdAndReturnedAtIsNull(copyId, userId)
                .orElseThrow(ResourceNotFoundException::new);

        Instant now = Instant.now();
        loan.markReturned(now);
        loan.getBookCopy().markAvailable();

        return saveRefreshAndMap(loan);
    }

    @Override
    @Transactional
    public LoanResponse renewLoan(UUID loanId, UUID userId) {
        LoanEntity loan = loanRepository
                .findByIdAndUserIdAndReturnedAtIsNull(loanId, userId)
                .orElseThrow(ResourceNotFoundException::new);

        Instant now = Instant.now();
        if (loan.isOverdue(now)) {
            throw new BusinessRuleViolationException("Overdue loans cannot be renewed");
        }

        if (loan.getRenewalCount() >= loanConfig.maxRenewals()) {
            throw new BusinessRuleViolationException("Loan has reached the maximum number of renewals");
        }

        loan.renew(loanConfig.renewalExtensionDays());

        return saveRefreshAndMap(loan);
    }

    private LoanResponse saveRefreshAndMap(LoanEntity loan) {
        LoanEntity saved = loanRepository.saveAndFlush(loan);
        entityManager.refresh(saved);
        return LoanResponse.from(saved);
    }
}