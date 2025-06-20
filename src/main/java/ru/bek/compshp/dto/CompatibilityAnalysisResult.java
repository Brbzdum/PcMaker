package ru.bek.compshp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.dto.CompatibilityIssue.IssueType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Результат детального анализа совместимости конфигурации
 */
@Data
@NoArgsConstructor
public class CompatibilityAnalysisResult {
    
    private boolean compatible;
    private List<CompatibilityIssue> issues = new ArrayList<>();
    private String overallStatus;
    private String statusMessage;
    private Double compatibilityScore; // 0.0 - 100.0
    private String recommendation;
    
    public CompatibilityAnalysisResult(boolean compatible) {
        this.compatible = compatible;
        updateStatus();
    }
    
    public void addIssue(CompatibilityIssue issue) {
        this.issues.add(issue);
        updateCompatibilityStatus();
        updateStatus();
    }
    
    public void addIssues(List<CompatibilityIssue> newIssues) {
        this.issues.addAll(newIssues);
        updateCompatibilityStatus();
        updateStatus();
    }
    
    /**
     * Получить критические ошибки
     */
    public List<CompatibilityIssue> getCriticalErrors() {
        return issues.stream()
                .filter(CompatibilityIssue::isCritical)
                .sorted(Comparator.comparing(CompatibilityIssue::getPriority))
                .collect(Collectors.toList());
    }
    
    /**
     * Получить предупреждения
     */
    public List<CompatibilityIssue> getWarnings() {
        return issues.stream()
                .filter(CompatibilityIssue::isWarning)
                .sorted(Comparator.comparing(CompatibilityIssue::getPriority))
                .collect(Collectors.toList());
    }
    
    /**
     * Получить рекомендации
     */
    public List<CompatibilityIssue> getRecommendations() {
        return issues.stream()
                .filter(CompatibilityIssue::isRecommendation)
                .sorted(Comparator.comparing(CompatibilityIssue::getPriority))
                .collect(Collectors.toList());
    }
    
    /**
     * Есть ли критические ошибки
     */
    public boolean hasCriticalErrors() {
        return issues.stream().anyMatch(CompatibilityIssue::isCritical);
    }
    
    /**
     * Есть ли предупреждения
     */
    public boolean hasWarnings() {
        return issues.stream().anyMatch(CompatibilityIssue::isWarning);
    }
    
    /**
     * Есть ли рекомендации
     */
    public boolean hasRecommendations() {
        return issues.stream().anyMatch(CompatibilityIssue::isRecommendation);
    }
    
    /**
     * Получить количество проблем по типам
     */
    public int getCriticalErrorCount() {
        return (int) issues.stream().filter(CompatibilityIssue::isCritical).count();
    }
    
    public int getWarningCount() {
        return (int) issues.stream().filter(CompatibilityIssue::isWarning).count();
    }
    
    public int getRecommendationCount() {
        return (int) issues.stream().filter(CompatibilityIssue::isRecommendation).count();
    }
    
    /**
     * Обновить статус совместимости на основе проблем
     */
    private void updateCompatibilityStatus() {
        this.compatible = !hasCriticalErrors();
    }
    
    /**
     * Обновить общий статус и сообщение
     */
    private void updateStatus() {
        if (hasCriticalErrors()) {
            this.overallStatus = "critical";
            this.statusMessage = String.format("Конфигурация несовместима. Обнаружено %d критических ошибок.", 
                    getCriticalErrorCount());
            this.compatibilityScore = 0.0;
        } else if (hasWarnings()) {
            this.overallStatus = "warning";
            this.statusMessage = String.format("Конфигурация работоспособна, но есть %d предупреждений.", 
                    getWarningCount());
            this.compatibilityScore = calculateCompatibilityScore();
        } else if (hasRecommendations()) {
            this.overallStatus = "good";
            this.statusMessage = "Конфигурация совместима. Есть рекомендации по оптимизации.";
            this.compatibilityScore = calculateCompatibilityScore();
        } else {
            this.overallStatus = "excellent";
            this.statusMessage = "Конфигурация полностью совместима!";
            this.compatibilityScore = 100.0;
        }
        
        generateRecommendation();
    }
    
    /**
     * Рассчитать оценку совместимости
     */
    private Double calculateCompatibilityScore() {
        if (hasCriticalErrors()) {
            return 0.0;
        }
        
        double score = 100.0;
        
        // Снижаем оценку за предупреждения
        score -= getWarningCount() * 15.0;
        
        // Снижаем оценку за рекомендации
        score -= getRecommendationCount() * 5.0;
        
        return Math.max(score, 10.0); // Минимальная оценка 10%
    }
    
    /**
     * Сгенерировать общую рекомендацию
     */
    private void generateRecommendation() {
        if (hasCriticalErrors()) {
            this.recommendation = "Необходимо устранить критические ошибки совместимости перед использованием конфигурации.";
        } else if (hasWarnings()) {
            this.recommendation = "Конфигурация работоспособна, но рекомендуется обратить внимание на предупреждения для оптимальной работы.";
        } else if (hasRecommendations()) {
            this.recommendation = "Отличная конфигурация! Рассмотрите рекомендации для дальнейшего улучшения.";
        } else {
            this.recommendation = "Идеальная конфигурация! Все компоненты полностью совместимы.";
        }
    }
    
    /**
     * Получить краткую сводку
     */
    public String getSummary() {
        if (issues.isEmpty()) {
            return "Проблем не обнаружено";
        }
        
        List<String> parts = new ArrayList<>();
        
        if (getCriticalErrorCount() > 0) {
            parts.add(getCriticalErrorCount() + " критических ошибок");
        }
        if (getWarningCount() > 0) {
            parts.add(getWarningCount() + " предупреждений");
        }
        if (getRecommendationCount() > 0) {
            parts.add(getRecommendationCount() + " рекомендаций");
        }
        
        return String.join(", ", parts);
    }
} 