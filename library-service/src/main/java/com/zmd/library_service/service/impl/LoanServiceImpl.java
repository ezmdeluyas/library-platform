package com.zmd.library_service.service.impl;

import com.zmd.library_service.config.LibraryConfig;
import com.zmd.library_service.dto.response.LoanResponse;
import com.zmd.library_service.entity.BookCopyEntity;
import com.zmd.library_service.entity.LoanEntity;
import com.zmd.library_service.exception.BusinessRuleViolationException;
import com.zmd.library_service.exception.ResourceNotFoundException;
import com.zmd.library_service.repository.BookCopyRepository;
import com.zmd.library_service.repository.LoanRepository;
import com.zmd.library_service.service.LoanService;
import lombok.RequiredArgsConstructor;
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
    private final LibraryConfig libraryConfig;

    @Override
    @Transactional
    public LoanResponse borrowBook(UUID copyId, UUID userId) {
        BookCopyEntity copy = bookCopyRepository.findByIdAndDeletedAtIsNull(copyId)
                .orElseThrow(ResourceNotFoundException::new);

        if (copy.getBook().getDeletedAt() != null) {
            throw new ResourceNotFoundException();
        }

        if (!copy.isAvailable()) {
            throw new BusinessRuleViolationException("Copy is not available");
        }

        long activeLoanCount = loanRepository.countByUserIdAndReturnedAtIsNull(userId);
        if (activeLoanCount >= libraryConfig.maxActive()) {
            throw new BusinessRuleViolationException("Maximum active loans reached");
        }

        Instant dueAt = Instant.now().plus(Duration.ofDays(libraryConfig.defaultDays()));

        copy.markBorrowed();

        LoanEntity loan = LoanEntity.createNew(
                UUID.randomUUID(),
                copy,
                userId,
                dueAt
        );

        LoanEntity saved = loanRepository.saveAndFlush(loan);
        LoanEntity reloaded = loanRepository.findById(saved.getId()).orElseThrow();

        return LoanResponse.from(reloaded);
    }

    @Override
    public LoanResponse returnBook(UUID copyId, UUID userId) {
        return null;
    }

    @Override
    public LoanResponse renewLoan(UUID loanId, UUID userId) {
        return null;
    }
}
