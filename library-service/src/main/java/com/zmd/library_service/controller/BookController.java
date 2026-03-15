package com.zmd.library_service.controller;

import com.zmd.library_service.dto.BookSearchFilter;
import com.zmd.library_service.dto.response.BookDetailResponse;
import com.zmd.library_service.dto.response.BookSummaryResponse;
import com.zmd.library_service.dto.response.PagedResponse;
import com.zmd.library_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private static final String DEFAULT_SORT_FIELD = "title";

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PagedResponse<BookSummaryResponse>> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Boolean available,
            @PageableDefault(page = 0, size = 10, sort = DEFAULT_SORT_FIELD) Pageable pageable
    ) {
        BookSearchFilter filter = new BookSearchFilter(
                title,
                author,
                categoryId,
                isbn,
                available
        );

        return ResponseEntity.ok(bookService.searchBooks(filter, pageable));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailResponse> getBookById(@PathVariable UUID bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }
}