package com.zmd.library_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "book_copies")
public class BookCopyEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "copy_code", unique = true, length = 255)
    private String copyCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "copy_status_enum")
    private CopyStatus status;

    @Column(nullable = false, length = 50)
    private String location;

    @Version
    @Column(nullable = false)
    private int version;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public static BookCopyEntity createNew(
            UUID id,
            BookEntity book,
            String copyCode,
            String location
    ) {
        BookCopyEntity copy = new BookCopyEntity();
        copy.id = id;
        copy.book = book;
        copy.copyCode = copyCode;
        copy.location = location;

        copy.status = CopyStatus.AVAILABLE;
        copy.version = 0;

        return copy;
    }

    public void markBorrowed() {
        this.status = CopyStatus.BORROWED;
    }

    public void markAvailable() {
        this.status = CopyStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return this.status == CopyStatus.AVAILABLE;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

}