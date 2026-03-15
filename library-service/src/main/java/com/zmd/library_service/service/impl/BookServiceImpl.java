package com.zmd.library_service.service.impl;

import com.zmd.library_service.dto.BookSearchFilter;
import com.zmd.library_service.dto.response.BookDetailResponse;
import com.zmd.library_service.dto.response.BookSummaryResponse;
import com.zmd.library_service.dto.response.PagedResponse;
import com.zmd.library_service.entity.BookEntity;
import com.zmd.library_service.exception.ResourceNotFoundException;
import com.zmd.library_service.mapper.BookCatalogMapper;
import com.zmd.library_service.mapper.PageResponseMapper;
import com.zmd.library_service.repository.BookRepository;
import com.zmd.library_service.service.BookService;
import com.zmd.library_service.specification.BookSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private static final String BOOK_NOT_FOUND_MESSAGE = "Book not found";

    private final BookRepository bookRepository;
    private final BookCatalogMapper bookCatalogMapper;

    @Override
    public PagedResponse<BookSummaryResponse> searchBooks(BookSearchFilter filter, Pageable pageable) {
        Specification<BookEntity> specification = BookSpecifications.withFilter(filter);

        Page<BookEntity> page = bookRepository.findAll(specification, pageable);
        Page<BookSummaryResponse> mappedPage = page.map(bookCatalogMapper::toSummaryResponse);

        return PageResponseMapper.from(mappedPage);
    }

    @Override
    public BookDetailResponse getBookById(UUID bookId) {
        BookEntity book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE));

        return bookCatalogMapper.toDetailResponse(book);
    }
}