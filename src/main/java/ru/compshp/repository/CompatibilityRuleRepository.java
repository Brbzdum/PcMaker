package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.CompatibilityRule;

// TODO: Репозиторий для правил совместимости
public interface CompatibilityRuleRepository extends JpaRepository<CompatibilityRule, Long> {
    // TODO: Методы поиска по типу компонента
} 