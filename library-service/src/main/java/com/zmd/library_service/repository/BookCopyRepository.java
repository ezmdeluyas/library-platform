package com.zmd.library_service.repository;

import com.zmd.library_service.entity.BookCopyEntity;
import com.zmd.library_service.entity.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopyEntity, UUID> {
    Optional<BookCopyEntity> findByIdAndDeleteAtIsNull(UUID id);
    long countByBookIdAndDeleteAtIsNull(UUID bookId, CopyStatus status);
}
