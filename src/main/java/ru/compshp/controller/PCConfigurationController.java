package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.PCConfigurationDto;
import ru.compshp.dto.ConfigComponentDto;
import ru.compshp.service.PCConfigurationService;
import ru.compshp.service.ProductService;

@RestController
@RequestMapping("/api/configurations")
@CrossOrigin
public class PCConfigurationController {
    private final PCConfigurationService pcConfigurationService;
    private final ProductService productService;

    public PCConfigurationController(PCConfigurationService pcConfigurationService, ProductService productService) {
        this.pcConfigurationService = pcConfigurationService;
        this.productService = productService;
    }

    // TODO: Получить список конфигураций пользователя
    @GetMapping
    public ResponseEntity<?> listConfigurations() {
        // TODO: Вернуть список конфигураций пользователя
        return ResponseEntity.ok(/* список конфигураций */ null);
    }

    // TODO: Создать новую конфигурацию
    @PostMapping
    public ResponseEntity<PCConfigurationDto> createConfiguration(@RequestBody PCConfigurationDto configurationDto) {
        // TODO: Создать новую конфигурацию
        return ResponseEntity.ok(/* configurationDto */ null);
    }

    // TODO: Получить конфигурацию по ID
    @GetMapping("/{id}")
    public ResponseEntity<PCConfigurationDto> getConfiguration(@PathVariable Long id) {
        // TODO: Получить конфигурацию по ID
        return ResponseEntity.ok(/* configurationDto */ null);
    }

    // TODO: Обновить конфигурацию
    @PutMapping("/{id}")
    public ResponseEntity<PCConfigurationDto> updateConfiguration(@PathVariable Long id, @RequestBody PCConfigurationDto configurationDto) {
        // TODO: Обновить конфигурацию
        return ResponseEntity.ok(/* configurationDto */ null);
    }

    // TODO: Удалить конфигурацию
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long id) {
        // TODO: Удалить конфигурацию
        return ResponseEntity.ok().build();
    }

    // TODO: Добавить компонент в конфигурацию
    @PostMapping("/{id}/components")
    public ResponseEntity<PCConfigurationDto> addComponent(@PathVariable Long id, @RequestBody ConfigComponentDto componentDto) {
        // TODO: Добавить компонент
        return ResponseEntity.ok(/* configurationDto */ null);
    }

    // TODO: Удалить компонент из конфигурации
    @DeleteMapping("/{id}/components/{productId}")
    public ResponseEntity<PCConfigurationDto> removeComponent(@PathVariable Long id, @PathVariable Long productId) {
        // TODO: Удалить компонент
        return ResponseEntity.ok(/* configurationDto */ null);
    }

    // TODO: Методы для проверки совместимости, расчета стоимости, экспорта, публикации и т.д.
} 