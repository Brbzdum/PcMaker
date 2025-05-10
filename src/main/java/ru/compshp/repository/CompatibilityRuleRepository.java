package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.enums.ComponentType;
import java.util.List;

// TODO: Репозиторий для правил совместимости
@Repository
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {
    List<CompatibilityRule> findBySourceTypeOrTargetType(ComponentType sourceType, ComponentType targetType);
    List<CompatibilityRule> findBySourceType(ComponentType sourceType);
    List<CompatibilityRule> findByTargetType(ComponentType targetType);
    List<CompatibilityRule> findBySourceTypeAndTargetType(ComponentType sourceType, ComponentType targetType);
} 