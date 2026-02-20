package com.zmd.auth_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "token_hash", unique = true, nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_token_id")
    private UUID replacedByTokenId;

    public static RefreshTokenEntity createNew(
            UUID id,
            UserEntity user,
            String tokenHash,
            Instant expiresAt
    ) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.id = id;
        entity.user = user;
        entity.tokenHash = tokenHash;
        entity.expiresAt = expiresAt;
        entity.revokedAt = null;
        entity.replacedByTokenId = null;
        return entity;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(Instant now, UUID replacedByTokenId) {
        this.revokedAt = now;
        this.replacedByTokenId = replacedByTokenId;
    }

    public void revoke(Instant now) {
        this.revokedAt = now;
    }

}
