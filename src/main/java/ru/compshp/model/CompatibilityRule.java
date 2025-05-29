package ru.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.compshp.model.enums.ComponentType;
import java.time.LocalDateTime;

/**
 * Модель правила совместимости компонентов
 */
@Entity
@Table(name = "compatibility_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityRule {

    /**
     * Оператор для правила совместимости
     */
    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        LESS_THAN,
        CONTAINS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private ComponentType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ComponentType targetType;

    @Column(name = "rule_type", nullable = false)
    private String ruleType;

    @Column(name = "source_property", nullable = false)
    private String sourceProperty;

    @Column(name = "target_property", nullable = false)
    private String targetProperty;

    @Column(name = "comparison_operator", nullable = false)
    private String comparisonOperator;

    @Column(name = "value_modifier")
    private String valueModifier;

    @Column
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

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