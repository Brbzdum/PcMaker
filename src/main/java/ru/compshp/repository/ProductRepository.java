package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Product;

// TODO: Репозиторий для товаров
public interface ProductRepository extends JpaRepository<Product, Long> {
    // TODO: Методы поиска по категории, производителю и названию
} 