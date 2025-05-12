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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", unique = true, nullable = false)
    private String cacheKey;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String result;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 