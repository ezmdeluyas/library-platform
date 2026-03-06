package com.zmd.library_service.repository;

import com.zmd.library_service.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorEntity, UUID> {
    Optional<AuthorEntity> findByIdAndDeletedAtIsNull(UUID id);
    List<AuthorEntity> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);
}
