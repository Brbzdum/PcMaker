package ru.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Модель компонента в конфигурации ПК
 */
@Entity
@Table(name = "config_components")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComponent {
    @EmbeddedId
    private ConfigComponentId id;

    @ManyToOne
    @MapsId("configId")
    @JoinColumn(name = "config_id")
    private PCConfiguration config;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Методы для совместимости с существующим кодом
    public void setConfiguration(PCConfiguration configuration) {
        this.config = configuration;
    }
    
    public PCConfiguration getConfiguration() {
        return this.config;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 