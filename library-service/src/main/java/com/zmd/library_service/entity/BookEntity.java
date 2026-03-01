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
@Table(name = "books")
public class BookEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(length = 32, unique = true)
    private String isbn;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public static BookEntity createNew(
            UUID id,
            String isbn,
            String title,
            String description,
            CategoryEntity category
    ) {
        BookEntity book = new BookEntity();
        book.id = id;
        book.isbn = isbn;
        book.title = title;
        book.description = description;
        book.category = category;
        return book;
    }

}