package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.User;

import java.util.List;

// TODO: Репозиторий для конфигураций ПК
@Repository
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    List<PCConfiguration> findByUser(User user);
    
    Page<PCConfiguration> findByUser(User user, Pageable pageable);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.user = :user AND pc.isCompatible = true")
    List<PCConfiguration> findUserCompatibleConfigurations(@Param("user") User user);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<PCConfiguration> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.isCompatible = true ORDER BY pc.createdAt DESC")
    List<PCConfiguration> findLatestCompatibleConfigurations(Pageable pageable);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.user = :user ORDER BY pc.createdAt DESC")
    List<PCConfiguration> findUserLatestConfigurations(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT COUNT(pc) FROM PCConfiguration pc WHERE pc.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.isCompatible = true AND pc.totalPrice <= :budget")
    List<PCConfiguration> findCompatibleConfigurationsWithinBudget(@Param("budget") Double budget);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.isCompatible = true AND pc.totalPrice <= :budget ORDER BY pc.performanceScore DESC")
    List<PCConfiguration> findBestPerformingConfigurationsWithinBudget(@Param("budget") Double budget, Pageable pageable);
} 