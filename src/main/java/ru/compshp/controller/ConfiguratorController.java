package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.compshp.service.ConfiguratorService;

@RestController
@RequestMapping("/api/configurator")
@RequiredArgsConstructor
@CrossOrigin
public class ConfiguratorController {
    private final ConfiguratorService configuratorService;

    // Получение доступных компонентов с учетом уже выбранных
    @GetMapping("/components")
    public ResponseEntity<?> getAvailableComponents(
            @RequestParam String type,
            @RequestParam(required = false) Long currentConfigId,
            @RequestParam(required = false) Double maxPrice) {
        return configuratorService.getAvailableComponents(type, currentConfigId, maxPrice);
    }

    // Создание новой конфигурации
    @PostMapping("/new")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createConfiguration(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return configuratorService.createConfiguration(name, description);
    }

    // Добавление компонента в конфигурацию
    @PostMapping("/{configId}/add-component")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addComponent(
            @PathVariable Long configId,
            @RequestParam Long productId) {
        return configuratorService.addComponent(configId, productId);
    }

    // Удаление компонента из конфигурации
    @DeleteMapping("/{configId}/remove-component")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeComponent(
            @PathVariable Long configId,
            @RequestParam String componentType) {
        return configuratorService.removeComponent(configId, componentType);
    }

    // Получение текущей конфигурации
    @GetMapping("/{configId}")
    public ResponseEntity<?> getConfiguration(@PathVariable Long configId) {
        return configuratorService.getConfiguration(configId);
    }

    // Получение списка сохраненных конфигураций пользователя
    @GetMapping("/saved")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSavedConfigurations() {
        return configuratorService.getSavedConfigurations();
    }

    // Удаление конфигурации
    @DeleteMapping("/{configId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long configId) {
        return configuratorService.deleteConfiguration(configId);
    }

    // Добавление конфигурации в корзину
    @PostMapping("/{configId}/add-to-cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addConfigurationToCart(@PathVariable Long configId) {
        return configuratorService.addConfigurationToCart(configId);
    }

    // Получение информации о совместимости
    @GetMapping("/{configId}/compatibility")
    public ResponseEntity<?> getCompatibilityInfo(@PathVariable Long configId) {
        return configuratorService.getCompatibilityInfo(configId);
    }

    // Расчет энергопотребления
    @GetMapping("/{configId}/power")
    public ResponseEntity<?> calculatePowerRequirement(@PathVariable Long configId) {
        return configuratorService.calculatePowerRequirement(configId);
    }

    // Эндпоинты для администраторов
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularConfigurations() {
        return configuratorService.getPopularConfigurations();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<?> getConfiguratorStatistics() {
        return configuratorService.getConfiguratorStatistics();
    }
} 