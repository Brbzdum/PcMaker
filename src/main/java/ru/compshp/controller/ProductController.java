package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ProductDto;
import ru.compshp.service.ProductService;
import ru.compshp.service.ReviewService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    // TODO: Получить список товаров с пагинацией и фильтрацией
    @GetMapping
    public ResponseEntity<?> listProducts(/* @RequestParam параметры фильтрации */) {
        // TODO: Реализовать получение списка товаров
        return ResponseEntity.ok(/* список товаров */ null);
    }

    // TODO: Получить товар по ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        // TODO: Реализовать получение товара по ID
        return ResponseEntity.ok(/* productDto */ null);
    }

    // TODO: Создать новый товар
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        // TODO: Реализовать создание товара
        return ResponseEntity.ok(/* productDto */ null);
    }

    // TODO: Обновить товар
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        // TODO: Реализовать обновление товара
        return ResponseEntity.ok(/* productDto */ null);
    }

    // TODO: Удалить товар
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        // TODO: Реализовать удаление товара
        return ResponseEntity.ok().build();
    }

    // TODO: Получить товары по категории
    @GetMapping("/category/{category}")
    public ResponseEntity<?> listByCategory(@PathVariable String category) {
        // TODO: Реализовать получение товаров по категории
        return ResponseEntity.ok(/* список товаров */ null);
    }

    // TODO: Получить товары по производителю
    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<?> listByManufacturer(@PathVariable Long manufacturerId) {
        // TODO: Реализовать получение товаров по производителю
        return ResponseEntity.ok(/* список товаров */ null);
    }

    // TODO: Методы поиска, фильтрации, сортировки, получения популярных и акционных товаров и т.д.
} 