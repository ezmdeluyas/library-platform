package com.zmd.auth_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false, insertable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private final Set<RoleEntity> roles = new HashSet<>();

    public static UserEntity createNew(
            UUID id,
            String email,
            String firstName,
            String lastName,
            String passwordHash,
            Set<RoleEntity> roles
    ) {
        UserEntity user = new UserEntity();
        user.id = id;
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        user.passwordHash = passwordHash;
        user.enabled = true;
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
        return user;
    }

}
