package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.compshp.model.enums.ComponentType;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    @Column(name = "check_condition", nullable = false, columnDefinition = "TEXT")
    private String checkCondition;

    @Column(name = "created_at", nullable = false)
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

    // TODO: Добавить метод для проверки условия совместимости
    // TODO: Добавить метод для валидации JSON условий
    // TODO: Добавить метод для применения правила к конкретным продуктам
    // TODO: Добавить метод для получения всех правил для определенного типа компонента
    // TODO: Добавить метод для проверки конфликтов правил

    public String getConditions() {
        return checkCondition;
    }

    public void setConditions(String conditions) {
        this.checkCondition = conditions;
    }
} 