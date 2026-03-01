package com.zmd.library_service.entity;

import jakarta.persistence.Column;import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;import java.util.UUID;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BookAuthorId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "book_id", nullable = false, updatable = false)
    private UUID bookId;

    @Column(name = "author_id", nullable = false, updatable = false)
    private UUID authorId;
}