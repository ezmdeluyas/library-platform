package com.zmd.library_service.repository;

import com.zmd.library_service.entity.BookAuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthorEntity, UUID> {
}
