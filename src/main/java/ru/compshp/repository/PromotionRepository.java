package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Promotion;
import ru.compshp.model.enums.PromotionType;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByPromotionType(PromotionType type);
    List<Promotion> findByIsActive(Boolean isActive);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= ?1 AND p.endDate >= ?1")
    List<Promotion> findActivePromotions(LocalDateTime currentDate);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.promotionType = ?1 AND p.startDate <= ?2 AND p.endDate >= ?2")
    List<Promotion> findActivePromotionsByType(PromotionType type, LocalDateTime currentDate);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.priority > 0 ORDER BY p.priority DESC")
    List<Promotion> findPrioritizedPromotions();
} 