package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.ConfigComponent;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import java.util.List;

// TODO: Репозиторий для компонентов конфигурации
public interface ConfigComponentRepository extends JpaRepository<ConfigComponent, Long> {
    List<ConfigComponent> findByConfiguration(PCConfiguration configuration);
    List<ConfigComponent> findByProduct(Product product);
} 