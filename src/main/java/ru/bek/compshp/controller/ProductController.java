package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.ProductDto;
import ru.bek.compshp.mapper.ProductMapper;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Товары", description = "API для управления товарами в магазине компьютерных комплектующих")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Создать новый товар", description = "Создает новый товар в базе данных")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно создан",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        log.debug("Создание нового товара: {}", productDto);
        Product product = productMapper.toEntity(productDto);
        Product createdProduct = productService.createProduct(product);
        ProductDto result = productMapper.toDto(createdProduct);
        log.debug("Товар успешно создан: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает товар по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар найден",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id) {
        log.debug("Получение товара по ID: {}", id);
        Product product = productService.getProductById(id);
        ProductDto result = productMapper.toDto(product);
        log.debug("Найден товар: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    @ApiResponse(responseCode = "200", description = "Список товаров получен успешно",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)))
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        log.debug("Получение списка всех товаров");
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров: {}", productDtos.size());
        log.debug("Первые 3 товара: {}", productDtos.stream().limit(3).collect(Collectors.toList()));
        return ResponseEntity.ok(productDtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет данные товара по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно обновлен",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Товар не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "ID товара", required = true) @PathVariable Long id, 
            @RequestBody ProductDto productDto) {
        log.debug("Обновление товара с ID {}: {}", id, productDto);
        Product existingProduct = productService.getProductById(id);
        Product updatedProduct = productMapper.updateEntityFromDto(existingProduct, productDto);
        Product savedProduct = productService.updateProduct(id, updatedProduct);
        ProductDto result = productMapper.toDto(savedProduct);
        log.debug("Товар успешно обновлен: {}", result);
        return ResponseEntity.ok(result);
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
        log.debug("Удаление товара с ID: {}", id);
        productService.deleteProduct(id);
        log.debug("Товар успешно удален");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Получить товары по категории", description = "Возвращает список товаров указанной категории")
    @ApiResponse(responseCode = "200", description = "Список товаров категории получен успешно")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @Parameter(description = "ID категории", required = true) @PathVariable Long categoryId) {
        log.debug("Получение товаров по категории: {}", categoryId);
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров в категории {}: {}", categoryId, productDtos.size());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    @Operation(summary = "Получить товары по производителю", description = "Возвращает список товаров указанного производителя")
    @ApiResponse(responseCode = "200", description = "Список товаров производителя получен успешно")
    public ResponseEntity<List<ProductDto>> getProductsByManufacturer(
            @Parameter(description = "ID производителя", required = true) @PathVariable Long manufacturerId) {
        log.debug("Получение товаров по производителю: {}", manufacturerId);
        List<Product> products = productService.getProductsByManufacturerId(manufacturerId);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров производителя {}: {}", manufacturerId, productDtos.size());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/component-type/{componentType}")
    @Operation(summary = "Получить товары по типу компонента", description = "Возвращает список товаров указанного типа компонента")
    @ApiResponse(responseCode = "200", description = "Список товаров по типу компонента получен успешно")
    public ResponseEntity<List<ProductDto>> getProductsByComponentType(
            @Parameter(description = "Тип компонента", required = true) @PathVariable ComponentType componentType) {
        log.debug("Получение товаров по типу компонента: {}", componentType);
        List<Product> products = productService.getProductsByType(componentType);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров типа {}: {}", componentType, productDtos.size());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/available")
    @Operation(summary = "Получить доступные товары", description = "Возвращает список товаров, имеющихся в наличии")
    @ApiResponse(responseCode = "200", description = "Список доступных товаров получен успешно")
    public ResponseEntity<List<ProductDto>> getAvailableProducts() {
        log.debug("Получение доступных товаров");
        List<Product> products = productService.getAvailableProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено доступных товаров: {}", productDtos.size());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Получить товары с низким запасом", 
               description = "Возвращает список товаров, количество которых меньше или равно указанному порогу")
    @ApiResponse(responseCode = "200", description = "Список товаров с низким запасом получен успешно")
    public ResponseEntity<List<ProductDto>> getLowStockProducts(
            @Parameter(description = "Пороговое значение для определения низкого запаса", example = "5")
            @RequestParam(defaultValue = "5") Integer threshold) {
        log.debug("Получение товаров с запасом меньше {}", threshold);
        List<Product> allProducts = productService.getAllProducts();
        List<Product> lowStockProducts = allProducts.stream()
                .filter(p -> p.getStock() <= threshold)
                .toList();
        List<ProductDto> productDtos = lowStockProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров с низким запасом: {}", productDtos.size());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/highly-rated")
    @Operation(summary = "Получить высоко оцененные товары", 
               description = "Возвращает список товаров с рейтингом не ниже указанного минимального значения")
    @ApiResponse(responseCode = "200", description = "Список высоко оцененных товаров получен успешно")
    public ResponseEntity<List<ProductDto>> getHighlyRatedProducts(
            @Parameter(description = "Минимальный рейтинг товара", example = "4.0")
            @RequestParam(defaultValue = "4.0") Double minRating) {
        log.debug("Получение товаров с рейтингом не ниже {}", minRating);
        List<Product> products = productService.getProductsByMinRating(minRating);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено товаров с высоким рейтингом: {}", productDtos.size());
        return ResponseEntity.ok(productDtos);
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
        log.debug("Обновление запаса товара с ID {}: новое количество {}", id, quantity);
        productService.updateStock(id, quantity);
        log.debug("Запас товара успешно обновлен");
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
        log.debug("Проверка доступности товара с ID {} в количестве {}", id, quantity);
        Product product = productService.getProductById(id);
        boolean isAvailable = product.getStock() >= quantity;
        log.debug("Товар {} в количестве {}: {}", id, quantity, isAvailable ? "доступен" : "недоступен");
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/pc-components")
    @Operation(summary = "Получить компоненты для ПК", 
               description = "Возвращает список товаров, которые являются компонентами для компьютера")
    @ApiResponse(responseCode = "200", description = "Список компонентов для ПК получен успешно")
    public ResponseEntity<List<ProductDto>> getPcComponentProducts() {
        log.debug("Получение компонентов для ПК");
        List<Product> products = productService.getPcComponentProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Найдено компонентов для ПК: {}", productDtos.size());
        return ResponseEntity.ok(productDtos);
    }
} 