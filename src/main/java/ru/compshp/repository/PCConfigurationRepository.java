package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.User;
import java.util.List;

// TODO: Репозиторий для конфигураций ПК
@Repository
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    // Поиск по пользователю
    List<PCConfiguration> findByUser(User user);
    Page<PCConfiguration> findByUser(User user, Pageable pageable);
    List<PCConfiguration> findByUserOrderByCreatedAtDesc(User user);
    
    // Поиск по совместимости
    List<PCConfiguration> findByUserAndIsCompatibleTrue(User user);
    
    // Поиск по цене
    List<PCConfiguration> findByTotalPriceBetween(Double minPrice, Double maxPrice);
    
    // Поиск последних конфигураций
    List<PCConfiguration> findByIsCompatibleTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Подсчет конфигураций пользователя
    long countByUser(User user);
    
    // Поиск по бюджету
    List<PCConfiguration> findByIsCompatibleTrueAndTotalPriceLessThanEqual(Double budget);
    List<PCConfiguration> findByIsCompatibleTrueAndTotalPriceLessThanEqualOrderByPerformanceScoreDesc(Double budget, Pageable pageable);
} 