package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.PCConfiguration;
import java.util.List;

// TODO: Репозиторий для конфигураций ПК
@Repository
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    List<PCConfiguration> findByUserId(Long userId);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.isCompatible = true")
    List<PCConfiguration> findCompatibleConfigurations();
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.userId = ?1 AND pc.isCompatible = true")
    List<PCConfiguration> findUserCompatibleConfigurations(Long userId);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.totalPrice BETWEEN ?1 AND ?2")
    List<PCConfiguration> findByPriceRange(Double minPrice, Double maxPrice);
} 