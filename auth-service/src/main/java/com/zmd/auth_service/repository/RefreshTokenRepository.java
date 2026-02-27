package com.zmd.auth_service.repository;

import com.zmd.auth_service.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update RefreshTokenEntity t
       set t.revokedAt = :now,
           t.replacedByTokenId = :replacementId
     where t.tokenHash = :hash
       and t.revokedAt is null
       and t.expiresAt > :now
    """)
    int rotate(
            @Param("hash") String hash,
            @Param("replacementId") UUID replacementId,
            @Param("now") Instant now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update RefreshTokenEntity t
       set t.revokedAt = :now
     where t.user.id = :userId
       and t.revokedAt is null
       and t.expiresAt > :now
    """)
    int revokeAllActiveByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now
    );

}
