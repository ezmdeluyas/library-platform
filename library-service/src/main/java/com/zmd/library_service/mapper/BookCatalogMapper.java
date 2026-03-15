package com.zmd.library_service.mapper;

import com.zmd.library_service.dto.response.BookDetailResponse;
import com.zmd.library_service.dto.response.BookSummaryResponse;
import com.zmd.library_service.entity.AuthorEntity;
import com.zmd.library_service.entity.BookAuthorEntity;
import com.zmd.library_service.entity.BookEntity;
import com.zmd.library_service.entity.CopyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BookCatalogMapper {

    @Mapping(target = "categoryName", expression = "java(extractCategoryName(book))")
    @Mapping(target = "authors", expression = "java(extractAuthorNames(book))")
    @Mapping(target = "totalCopies", expression = "java(countActiveCopies(book))")
    @Mapping(target = "availableCopies", expression = "java(countAvailableCopies(book))")
    @Mapping(target = "available", expression = "java(countAvailableCopies(book) > 0)")
    BookSummaryResponse toSummaryResponse(BookEntity book);

    @Mapping(target = "categoryName", expression = "java(extractCategoryName(book))")
    @Mapping(target = "authors", expression = "java(extractAuthorNames(book))")
    @Mapping(target = "totalCopies", expression = "java(countActiveCopies(book))")
    @Mapping(target = "availableCopies", expression = "java(countAvailableCopies(book))")
    @Mapping(target = "available", expression = "java(countAvailableCopies(book) > 0)")
    BookDetailResponse toDetailResponse(BookEntity book);

    default String extractCategoryName(BookEntity book) {
        return book.getCategory() != null ? book.getCategory().getName() : null;
    }

    default List<String> extractAuthorNames(BookEntity book) {
        if (book.getBookAuthors() == null) {
            return List.of();
        }

        return book.getBookAuthors().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        BookAuthorEntity::getAuthorOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(BookAuthorEntity::getAuthor)
                .filter(Objects::nonNull)
                .map(AuthorEntity::getName)
                .filter(Objects::nonNull)
                .toList();
    }

    default int countActiveCopies(BookEntity book) {
        if (book.getCopies() == null) {
            return 0;
        }

        return (int) book.getCopies().stream()
                .filter(Objects::nonNull)
                .filter(copy -> copy.getDeletedAt() == null)
                .count();
    }

    default int countAvailableCopies(BookEntity book) {
        if (book.getCopies() == null) {
            return 0;
        }

        return (int) book.getCopies().stream()
                .filter(Objects::nonNull)
                .filter(copy -> copy.getDeletedAt() == null)
                .filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE)
                .count();
    }
}