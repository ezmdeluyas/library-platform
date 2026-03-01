package com.zmd.library_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopyEntity bookCopy;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "borrowed_at", nullable = false, insertable = false, updatable = false)
    private Instant borrowedAt;

    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Column(name = "renewal_count", nullable = false)
    private short renewalCount;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    public static LoanEntity createNew(
            UUID id,
            BookCopyEntity bookCopy,
            UUID userId,
            Instant dueAt
    ) {
        LoanEntity loan = new LoanEntity();
        loan.id = id;
        loan.bookCopy = bookCopy;
        loan.userId = userId;
        loan.dueAt = dueAt;
        return loan;
    }

}