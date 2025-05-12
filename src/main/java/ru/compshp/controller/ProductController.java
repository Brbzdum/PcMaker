package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ProductDto;
import ru.compshp.service.ProductService;
import ru.compshp.service.ReviewService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;

    // Получение списка товаров с фильтрацией
    @GetMapping
    public ResponseEntity<?> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProducts(category, manufacturer, minPrice, maxPrice, sortBy, page, size);
    }

    // Получение товара по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    // Поиск товаров
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(query, page, size);
    }

    // Популярные товары
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularProducts() {
        return productService.getPopularProducts();
    }

    // Новые поступления
    @GetMapping("/new-arrivals")
    public ResponseEntity<?> getNewArrivals() {
        return productService.getNewArrivals();
    }

    // Товары по категории
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByCategory(category, page, size);
    }

    // Товары по производителю
    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<?> getProductsByManufacturer(
            @PathVariable Long manufacturerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByManufacturer(manufacturerId, page, size);
    }

    // Административные эндпоинты
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
} 