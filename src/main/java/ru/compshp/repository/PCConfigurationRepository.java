package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.User;
import java.util.List;

// TODO: Репозиторий для конфигураций ПК
@Repository
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    List<PCConfiguration> findByUser(User user);

    List<PCConfiguration> findTop10ByOrderByCreatedAtDesc();
} 