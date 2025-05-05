package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.Product;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.ReviewRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    // TODO: Добавить метод для создания товара
    // TODO: Добавить метод для обновления товара
    // TODO: Добавить метод для удаления товара
    // TODO: Добавить метод для получения товара по ID
    // TODO: Добавить метод для получения всех товаров
    // TODO: Добавить метод для получения товаров по категории
    // TODO: Добавить метод для получения товаров по производителю
    // TODO: Добавить метод для поиска товаров
    // TODO: Добавить метод для фильтрации товаров
    // TODO: Добавить метод для сортировки товаров
    // TODO: Добавить метод для получения популярных товаров
    // TODO: Добавить метод для получения акционных товаров
    // TODO: Добавить метод для обновления рейтинга товара
    // TODO: Добавить метод для проверки наличия товара
    // TODO: Добавить метод для обновления количества товара
    // TODO: Добавить метод для получения похожих товаров
    // TODO: Добавить метод для экспорта каталога
} 