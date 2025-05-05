package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "compatibility_rules")
public class CompatibilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private ComponentType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ComponentType targetType;

    @Column(name = "check_condition", columnDefinition = "jsonb", nullable = false)
    private String checkCondition;

    private String description;

    // TODO: Добавить метод для проверки условия совместимости
    // TODO: Добавить метод для валидации JSON условий
    // TODO: Добавить метод для применения правила к конкретным продуктам
    // TODO: Добавить метод для получения всех правил для определенного типа компонента
    // TODO: Добавить метод для проверки конфликтов правил
} 