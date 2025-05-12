package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.compshp.model.enums.ComponentType;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recommended_config_components")
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedConfigComponent {
    @EmbeddedId
    private RecommendedConfigComponentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("configId")
    @JoinColumn(name = "config_id")
    private RecommendedConfig config;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false)
    private ComponentType componentType;

    @Column(name = "specs_requirements", columnDefinition = "jsonb", nullable = false)
    private String specsRequirements;

    private Integer priority = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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