package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.enums.ComponentType;
import java.util.List;

// TODO: Репозиторий для правил совместимости
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {
    List<CompatibilityRule> findBySourceType(ComponentType sourceType);
    List<CompatibilityRule> findByTargetType(ComponentType targetType);
} 