package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.model.enums.ComponentType;
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
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_EQUALS(">="),
        LESS_THAN_EQUALS("<="),
        CONTAINS("CONTAINS");
        
        private final String value;
        
        Operator(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Operator fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Operator value cannot be null");
            }
            
            // Сначала проверяем, соответствует ли значение имени перечисления
            try {
                return Operator.valueOf(value);
            } catch (IllegalArgumentException ignored) {
                // Если не соответствует имени, проверяем символьное представление
                for (Operator operator : Operator.values()) {
                    if (operator.value.equals(value)) {
                        return operator;
                    }
                }
                throw new IllegalArgumentException("Unknown operator: " + value);
            }
        }
    }
    
    /**
     * Тип правила совместимости
     */
    public enum RuleType {
        EXACT_MATCH,
        RANGE_CHECK,
        COMPATIBILITY_LIST,
        GREATER_THAN,
        LESS_THAN,
        SUBSET_CHECK
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

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @Column(name = "source_property", nullable = false)
    private String sourceProperty;

    @Column(name = "target_property", nullable = false)
    private String targetProperty;

    @Column(name = "comparison_operator", nullable = false)
    private String comparisonOperator;

    @Transient
    private Operator operator;
    
    @PostLoad
    void fillOperator() {
        if (comparisonOperator != null) {
            this.operator = Operator.fromString(comparisonOperator);
        }
    }

    @Column(name = "value_modifier")
    private String valueModifier;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Заполняем comparisonOperator из operator
        if (operator != null) {
            this.comparisonOperator = operator.getValue();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Заполняем comparisonOperator из operator
        if (operator != null) {
            this.comparisonOperator = operator.getValue();
        }
    }
} 