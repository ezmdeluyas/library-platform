package com.zmd.library_service.service;

import com.zmd.library_service.dto.BookSearchFilter;
import com.zmd.library_service.dto.response.BookDetailResponse;
import com.zmd.library_service.dto.response.BookSummaryResponse;
import com.zmd.library_service.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookService {

    PagedResponse<BookSummaryResponse> searchBooks(BookSearchFilter filter, Pageable pageable);

    BookDetailResponse getBookById(UUID bookId);
}