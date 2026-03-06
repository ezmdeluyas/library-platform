package com.zmd.library_service.repository;

import com.zmd.library_service.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {
    long countByUserIdAndReturnedAtIsNull(UUID userId);
    Optional<LoanEntity> findByBookCopyIdAndReturnedAtIsNull(UUID bookCopyId);
    Optional<LoanEntity> findByBookCopyIdAndUserIdAndReturnedAtIsNull(UUID bookCopyId, UUID userId);
    Optional<LoanEntity> findByIdAndUserIdAndReturnedAtIsNull(UUID id, UUID userId);
    List<LoanEntity> findByUserIdAndReturnedAtIsNull(UUID userId);
}
