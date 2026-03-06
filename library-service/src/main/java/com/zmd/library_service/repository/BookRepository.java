package com.zmd.library_service.repository;

import com.zmd.library_service.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {
    Optional<BookEntity> findByIdAndDeleteAtIsNull(UUID id);
}
