package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.RecommendedConfig;
import java.util.List;

@Repository
public interface RecommendedConfigRepository extends JpaRepository<RecommendedConfig, Long> {
    List<RecommendedConfig> findByTargetUsage(String targetUsage);
    List<RecommendedConfig> findByIsFeatured(Boolean isFeatured);
    
    @Query("SELECT rc FROM RecommendedConfig rc WHERE rc.priceRangeMin <= ?1 AND rc.priceRangeMax >= ?1")
    List<RecommendedConfig> findByPrice(Double price);
    
    @Query("SELECT rc FROM RecommendedConfig rc WHERE rc.performanceScore >= ?1")
    List<RecommendedConfig> findByMinPerformanceScore(Integer minScore);
    
    @Query("SELECT rc FROM RecommendedConfig rc WHERE rc.targetUsage = ?1 AND rc.priceRangeMin <= ?2 AND rc.priceRangeMax >= ?2")
    List<RecommendedConfig> findByTargetUsageAndPrice(String targetUsage, Double price);
} 