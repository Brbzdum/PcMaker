package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Товары", description = "API для управления товарами в магазине компьютерных комплектующих")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Создать новый товар", description = "Создает новый товар в базе данных")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно создан",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает товар по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар найден",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    @ApiResponse(responseCode = "200", description = "Список товаров получен успешно",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет данные товара по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно обновлен",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Товар не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id, 
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар", description = "Удаляет товар по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно удален"),
        @ApiResponse(responseCode = "404", description = "Товар не найден"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Получить товары по категории", description = "Возвращает список товаров указанной категории")
    @ApiResponse(responseCode = "200", description = "Список товаров категории получен успешно")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "ID категории", required = true) @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    @Operation(summary = "Получить товары по производителю", description = "Возвращает список товаров указанного производителя")
    @ApiResponse(responseCode = "200", description = "Список товаров производителя получен успешно")
    public ResponseEntity<List<Product>> getProductsByManufacturer(
            @Parameter(description = "ID производителя", required = true) @PathVariable Long manufacturerId) {
        return ResponseEntity.ok(productService.getProductsByManufacturerId(manufacturerId));
    }

    @GetMapping("/component-type/{componentType}")
    @Operation(summary = "Получить товары по типу компонента", description = "Возвращает список товаров указанного типа компонента")
    @ApiResponse(responseCode = "200", description = "Список товаров по типу компонента получен успешно")
    public ResponseEntity<List<Product>> getProductsByComponentType(
            @Parameter(description = "Тип компонента", required = true) @PathVariable ComponentType componentType) {
        return ResponseEntity.ok(productService.getProductsByType(componentType));
    }

    @GetMapping("/available")
    @Operation(summary = "Получить доступные товары", description = "Возвращает список товаров, имеющихся в наличии")
    @ApiResponse(responseCode = "200", description = "Список доступных товаров получен успешно")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Получить товары с низким запасом", 
               description = "Возвращает список товаров, количество которых меньше или равно указанному порогу")
    @ApiResponse(responseCode = "200", description = "Список товаров с низким запасом получен успешно")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "Пороговое значение для определения низкого запаса", example = "5")
            @RequestParam(defaultValue = "5") Integer threshold) {
        // Since there's no direct method for low stock products,
        // we'll get all products and filter them on the server side
        List<Product> allProducts = productService.getAllProducts();
        List<Product> lowStockProducts = allProducts.stream()
                .filter(p -> p.getStock() <= threshold)
                .toList();
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/highly-rated")
    @Operation(summary = "Получить высоко оцененные товары", 
               description = "Возвращает список товаров с рейтингом не ниже указанного минимального значения")
    @ApiResponse(responseCode = "200", description = "Список высоко оцененных товаров получен успешно")
    public ResponseEntity<List<Product>> getHighlyRatedProducts(
            @Parameter(description = "Минимальный рейтинг товара", example = "4.0")
            @RequestParam(defaultValue = "4.0") Double minRating) {
        return ResponseEntity.ok(productService.getProductsByMinRating(minRating));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Обновить запас товара", description = "Устанавливает указанное количество товара на складе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запас товара успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Товар не найден"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<Void> updateStock(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id,
            @Parameter(description = "Новое количество товара", required = true, example = "10") 
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/available")
    @Operation(summary = "Проверить доступность товара", 
               description = "Проверяет, доступно ли указанное количество товара на складе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Информация о доступности товара получена"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    public ResponseEntity<Boolean> isProductAvailable(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id,
            @Parameter(description = "Требуемое количество товара", required = true, example = "1") 
            @RequestParam Integer quantity) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product.getStock() >= quantity);
    }

    @GetMapping("/pc-components")
    @Operation(summary = "Получить компоненты для ПК", 
               description = "Возвращает список товаров, которые являются компонентами для компьютера")
    @ApiResponse(responseCode = "200", description = "Список компонентов для ПК получен успешно")
    public ResponseEntity<List<Product>> getPcComponentProducts() {
        return ResponseEntity.ok(productService.getPcComponentProducts());
    }
} 