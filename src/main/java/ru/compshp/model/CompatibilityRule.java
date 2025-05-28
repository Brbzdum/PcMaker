package ru.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.compshp.model.enums.ComponentType;

/**
 * Модель правила совместимости компонентов
 */
@Entity
@Table(name = "compatibility_rules")
@Data
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

    @Column(name = "source_spec_key", nullable = false)
    private String sourceSpecKey;

    @Column(name = "target_spec_key", nullable = false)
    private String targetSpecKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operator operator;

    @Column(length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
} 