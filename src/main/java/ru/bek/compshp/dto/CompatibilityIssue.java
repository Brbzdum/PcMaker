package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления проблемы совместимости
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityIssue {
    
    /**
     * Тип проблемы совместимости
     */
    public enum IssueType {
        CRITICAL_ERROR("Критическая ошибка", "error"),
        WARNING("Предупреждение", "warning"), 
        RECOMMENDATION("Рекомендация", "info");
        
        private final String displayName;
        private final String cssClass;
        
        IssueType(String displayName, String cssClass) {
            this.displayName = displayName;
            this.cssClass = cssClass;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCssClass() { return cssClass; }
    }
    
    /**
     * Категория проблемы
     */
    public enum IssueCategory {
        COMPATIBILITY("Совместимость"),
        PERFORMANCE("Производительность"),
        POWER("Энергопотребление"),
        THERMAL("Тепловыделение"),
        PHYSICAL("Физические размеры"),
        BALANCE("Баланс компонентов"),
        MISSING_COMPONENT("Отсутствующие компоненты");
        
        private final String displayName;
        
        IssueCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    private IssueType type;
    private IssueCategory category;
    private String title;
    private String description;
    private String component1;
    private String component2;
    private String recommendation;
    private String technicalDetails;
    private Integer priority; // 1-5, где 1 - наивысший приоритет
    
    public CompatibilityIssue(IssueType type, IssueCategory category, String title, String description) {
        this.type = type;
        this.category = category;
        this.title = title;
        this.description = description;
        this.priority = type == IssueType.CRITICAL_ERROR ? 1 : 
                       type == IssueType.WARNING ? 2 : 3;
    }
    
    public CompatibilityIssue(IssueType type, IssueCategory category, String title, String description, 
                             String component1, String component2) {
        this(type, category, title, description);
        this.component1 = component1;
        this.component2 = component2;
    }
    
    public boolean isCritical() {
        return type == IssueType.CRITICAL_ERROR;
    }
    
    public boolean isWarning() {
        return type == IssueType.WARNING;
    }
    
    public boolean isRecommendation() {
        return type == IssueType.RECOMMENDATION;
    }
} 