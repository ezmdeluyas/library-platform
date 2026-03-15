package com.zmd.library_service.dto.response;

import java.util.List;
import java.util.UUID;

public record BookDetailResponse(
        UUID id,
        String isbn,
        String title,
        String description,
        String categoryName,
        List<String> authors,
        int totalCopies,
        int availableCopies,
        boolean available
) {}
