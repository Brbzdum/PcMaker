package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.ProductCompatibility;
import ru.compshp.model.Product;
import ru.compshp.model.CompatibilityRule;
import java.util.List;
import java.util.Optional;

// TODO: Репозиторий для совместимости продуктов
public interface ProductCompatibilityRepository extends JpaRepository<ProductCompatibility, Long> {
    List<ProductCompatibility> findBySourceProduct(Product sourceProduct);
    List<ProductCompatibility> findByTargetProduct(Product targetProduct);
    List<ProductCompatibility> findByRule(CompatibilityRule rule);
    Optional<ProductCompatibility> findBySourceProductAndTargetProduct(Product source, Product target);
} 