package com.zmd.library_service.specification;

import com.zmd.library_service.dto.BookSearchFilter;
import com.zmd.library_service.entity.BookEntity;
import com.zmd.library_service.entity.CopyStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookSpecifications {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ISBN = "isbn";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String AUTHOR = "author";
    private static final String CATEGORY = "category";
    private static final String COPIES = "copies";
    private static final String DELETED_AT = "deletedAt";
    private static final String BOOK_AUTHORS = "bookAuthors";

    public static Specification<BookEntity> withFilter(BookSearchFilter filter) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get(DELETED_AT)));

            Predicate titlePredicate = hasTitle(root, cb, filter.title());
            if (titlePredicate != null) {
                predicates.add(titlePredicate);
            }

            Predicate authorPredicate = hasAuthor(root, query.subquery(UUID.class), cb, filter.author());
            if (authorPredicate != null) {
                predicates.add(authorPredicate);
            }

            Predicate categoryPredicate = hasCategoryId(root, cb, filter.categoryId());
            if (categoryPredicate != null) {
                predicates.add(categoryPredicate);
            }

            Predicate isbnPredicate = hasIsbn(root, cb, filter.isbn());
            if (isbnPredicate != null) {
                predicates.add(isbnPredicate);
            }

            Predicate availabilityPredicate = hasAvailability(root, query.subquery(UUID.class), cb, filter.available());
            if (availabilityPredicate != null) {
                predicates.add(availabilityPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate hasTitle(
            Root<BookEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String title
    ) {
        if (isBlank(title)) {
            return null;
        }

        return cb.like(
                cb.lower(root.get(TITLE)),
                "%" + title.trim().toLowerCase() + "%"
        );
    }

    private static Predicate hasAuthor(
            Root<BookEntity> root,
            Subquery<UUID> subquery,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String author
    ) {
        if (isBlank(author)) {
            return null;
        }

        Root<BookEntity> subRoot = subquery.from(BookEntity.class);
        Join<Object, Object> bookAuthors = subRoot.join(BOOK_AUTHORS, JoinType.INNER);
        Join<Object, Object> authorJoin = bookAuthors.join(AUTHOR, JoinType.INNER);

        subquery.select(subRoot.get(ID))
                .where(
                        cb.equal(subRoot.get(ID), root.get(ID)),
                        cb.isNull(subRoot.get(DELETED_AT)),
                        cb.like(
                                cb.lower(authorJoin.get(NAME)),
                                "%" + author.trim().toLowerCase() + "%"
                        )
                );

        return cb.exists(subquery);
    }

    private static Predicate hasCategoryId(
            Root<BookEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            UUID categoryId
    ) {
        if (categoryId == null) {
            return null;
        }

        return cb.equal(root.get(CATEGORY).get(ID), categoryId);
    }

    private static Predicate hasIsbn(
            Root<BookEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String isbn
    ) {
        if (isBlank(isbn)) {
            return null;
        }

        return cb.equal(
                cb.lower(root.get(ISBN)),
                isbn.trim().toLowerCase()
        );
    }

    private static Predicate hasAvailability(
            Root<BookEntity> root,
            Subquery<UUID> subquery,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            Boolean available
    ) {
        if (available == null) {
            return null;
        }

        Root<BookEntity> subRoot = subquery.from(BookEntity.class);
        Join<Object, Object> copies = subRoot.join(COPIES, JoinType.INNER);

        subquery.select(subRoot.get(ID))
                .where(
                        cb.equal(subRoot.get(ID), root.get(ID)),
                        cb.isNull(subRoot.get(DELETED_AT)),
                        cb.isNull(copies.get(DELETED_AT)),
                        cb.equal(copies.get(STATUS), CopyStatus.AVAILABLE)
                );

        return available
                ? cb.exists(subquery)
                : cb.not(cb.exists(subquery));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}