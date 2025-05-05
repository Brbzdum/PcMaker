package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.PCConfiguration;

// TODO: Репозиторий для конфигураций ПК
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    // TODO: Методы поиска по пользователю
} 