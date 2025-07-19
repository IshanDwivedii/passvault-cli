package com.ishan.passvault.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "passwordEntries")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Master password is required")
    @Column(nullable = false, length = 255)
    private String masterPasswordHash;

    @Column(nullable = false, length = 64)
    private String salt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("serviceName ASC")
    @Builder.Default
    private List<PasswordEntry> passwordEntries = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastLogin = LocalDateTime.now();
    }

    // Helper methods
    public void addPasswordEntry(PasswordEntry entry) {
        passwordEntries.add(entry);
        entry.setUser(this);
    }

    public void removePasswordEntry(PasswordEntry entry) {
        passwordEntries.remove(entry);
        entry.setUser(null);
    }
}
