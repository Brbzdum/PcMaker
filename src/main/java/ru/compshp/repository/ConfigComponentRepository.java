package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.ConfigComponent;

// TODO: Репозиторий для компонентов конфигурации
public interface ConfigComponentRepository extends JpaRepository<ConfigComponent, Long> {
    // TODO: Методы поиска по конфигурации и товару
} 