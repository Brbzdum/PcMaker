package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.service.ProductService;
import ru.compshp.service.PCConfigurationService;

@RestController
@RequestMapping("/api/home")
@CrossOrigin
public class HomeController {
    private final ProductService productService;
    private final PCConfigurationService pcConfigurationService;

    public HomeController(ProductService productService, PCConfigurationService pcConfigurationService) {
        this.productService = productService;
        this.pcConfigurationService = pcConfigurationService;
    }

    // TODO: Получить главную информацию (популярные товары, последние конфигурации, акции)
    @GetMapping
    public ResponseEntity<?> getHomeInfo() {
        // TODO: Вернуть главную информацию
        return ResponseEntity.ok(/* главная информация */ null);
    }

    // TODO: Получить информацию о магазине
    @GetMapping("/about")
    public ResponseEntity<?> about() {
        // TODO: Вернуть информацию о магазине
        return ResponseEntity.ok(/* about */ null);
    }

    // TODO: Получить контакты
    @GetMapping("/contacts")
    public ResponseEntity<?> contacts() {
        // TODO: Вернуть контакты
        return ResponseEntity.ok(/* contacts */ null);
    }

    // TODO: Методы для поиска, фильтрации, получения категорий, производителей, новостей, акций, FAQ и т.д.
} 