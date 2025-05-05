package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ProductDto;
import ru.compshp.service.ManufacturerService;
import ru.compshp.service.ProductService;

@RestController
@RequestMapping("/api/manufacturers")
@CrossOrigin
public class ManufacturerController {
    private final ManufacturerService manufacturerService;
    private final ProductService productService;

    public ManufacturerController(ManufacturerService manufacturerService, ProductService productService) {
        this.manufacturerService = manufacturerService;
        this.productService = productService;
    }

    // TODO: Получить список производителей
    @GetMapping
    public ResponseEntity<?> listManufacturers() {
        // TODO: Вернуть список производителей
        return ResponseEntity.ok(/* список производителей */ null);
    }

    // TODO: Получить информацию о производителе
    @GetMapping("/{id}")
    public ResponseEntity<?> getManufacturer(@PathVariable Long id) {
        // TODO: Вернуть информацию о производителе
        return ResponseEntity.ok(/* производитель */ null);
    }

    // TODO: Создать производителя
    @PostMapping
    public ResponseEntity<?> createManufacturer(@RequestBody Object manufacturerDto) {
        // TODO: Создать производителя
        return ResponseEntity.ok(/* производитель */ null);
    }

    // TODO: Обновить производителя
    @PutMapping("/{id}")
    public ResponseEntity<?> updateManufacturer(@PathVariable Long id, @RequestBody Object manufacturerDto) {
        // TODO: Обновить производителя
        return ResponseEntity.ok(/* производитель */ null);
    }

    // TODO: Удалить производителя
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManufacturer(@PathVariable Long id) {
        // TODO: Удалить производителя
        return ResponseEntity.ok().build();
    }

    // TODO: Получить товары производителя
    @GetMapping("/{id}/products")
    public ResponseEntity<?> listManufacturerProducts(@PathVariable Long id) {
        // TODO: Вернуть список товаров производителя
        return ResponseEntity.ok(/* список товаров */ null);
    }

    // TODO: Методы для получения статистики, рейтинга, популярных товаров и т.д.
} 