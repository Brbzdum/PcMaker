package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.CompatibilityCache;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с кешем совместимости компонентов
 */
@Repository
public interface CompatibilityCacheRepository extends JpaRepository<CompatibilityCache, String> {
    
    /**
     * Находит записи кеша по хешу конфигурации
     */
    List<CompatibilityCache> findByConfigHash(String configHash);
    
    /**
     * Удаляет устаревшие записи кеша
     */
    void deleteByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Находит записи кеша по статусу совместимости
     */
    List<CompatibilityCache> findByIsCompatible(boolean isCompatible);
} 