package com.zmd.library_service.dto;

import java.util.UUID;

public record BookSearchFilter(
        String title,
        String author,
        UUID categoryId,
        String isbn,
        Boolean available
) {
}