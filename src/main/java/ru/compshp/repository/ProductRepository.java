package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Product;
import ru.compshp.model.Manufacturer;
import ru.compshp.model.enums.ProductCategory;
import java.util.List;

// TODO: Репозиторий для товаров
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByManufacturer(Manufacturer manufacturer);
    List<Product> findByTitleContainingIgnoreCase(String title);
} 