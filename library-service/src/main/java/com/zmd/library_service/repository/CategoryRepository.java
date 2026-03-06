package com.zmd.library_service.repository;

import com.zmd.library_service.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findByIdAndDeletedAtIsNull(UUID id);
    Optional<CategoryEntity> findByNameAndDeletedAtIsNull(String name);
}
