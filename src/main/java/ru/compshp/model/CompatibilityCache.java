package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Сущность для хранения кеша результатов проверки совместимости
 */
@Data
@Entity
@Table(name = "compatibility_cache")
public class CompatibilityCache {
    @Id
    @Column(name = "config_hash", length = 64)
    private String configHash;

    @Column(name = "is_compatible", nullable = false)
    private boolean isCompatible;

    @Column(name = "errors", columnDefinition = "jsonb")
    private String errors;

    @Column(name = "warnings", columnDefinition = "jsonb")
    private String warnings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 