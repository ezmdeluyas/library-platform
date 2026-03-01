package com.zmd.library_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "book_authors")
public class BookAuthorEntity {

    @EmbeddedId
    private BookAuthorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorId")
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @Column(name = "author_order")
    private Short authorOrder;

    public static BookAuthorEntity createNew(
            BookEntity book,
            AuthorEntity author,
            Short authorOrder
    ) {
        BookAuthorEntity link = new BookAuthorEntity();
        link.book = book;
        link.author = author;
        link.id = new BookAuthorId(book.getId(), author.getId());
        link.authorOrder = authorOrder;
        return link;
    }

}