package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.CompatibilityRule;
import ru.bek.compshp.model.enums.ComponentType;

import java.util.List;

/**
 * Репозиторий для правил совместимости компонентов
 */
@Repository
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {
    
    /**
     * Находит правила по типу исходного компонента и типу целевого компонента
     * @param sourceType тип исходного компонента
     * @param targetType тип целевого компонента
     * @return список правил совместимости
     */
    List<CompatibilityRule> findBySourceTypeAndTargetType(ComponentType sourceType, ComponentType targetType);
    
    /**
     * Находит правила по типу исходного компонента
     * @param sourceType тип исходного компонента
     * @return список правил совместимости
     */
    List<CompatibilityRule> findBySourceType(ComponentType sourceType);
    
    /**
     * Находит правила по типу целевого компонента
     * @param targetType тип целевого компонента
     * @return список правил совместимости
     */
    List<CompatibilityRule> findByTargetType(ComponentType targetType);
    
    /**
     * Находит активные правила по типу исходного компонента и типу целевого компонента
     * @param sourceType тип исходного компонента
     * @param targetType тип целевого компонента
     * @return список активных правил совместимости
     */
    List<CompatibilityRule> findBySourceTypeAndTargetTypeAndIsActiveTrue(ComponentType sourceType, ComponentType targetType);
} 