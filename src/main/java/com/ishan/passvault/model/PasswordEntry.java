package com.ishan.passvault.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_entries", indexes = {
    @Index(name = "idx_user_service", columnList = "user_id, service_name"),
    @Index(name = "idx_service_name", columnList = "service_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name must not exceed 100 characters")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Encrypted password is required")
    @Column(name = "encrypted_password", nullable = false, length = 1000)
    private String encryptedPassword;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(length = 500)
    private String notes;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(length = 50)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_password_entry_user"))
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to update last accessed time
    public void markAsAccessed() {
        this.lastAccessed = LocalDateTime.now();
    }
}
