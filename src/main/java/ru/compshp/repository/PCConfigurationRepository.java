package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.User;
import java.util.List;

// TODO: Репозиторий для конфигураций ПК
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    List<PCConfiguration> findByUser(User user);
} 