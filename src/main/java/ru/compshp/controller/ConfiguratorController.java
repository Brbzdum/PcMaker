package ru.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ComponentAddRequest;
import ru.compshp.dto.ConfigResponse;
import ru.compshp.dto.ConfigurationCreateRequest;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.ConfiguratorService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Tag(name = "Конфигуратор ПК", description = "API для управления конфигурациями ПК")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;

    @PostMapping
    @Operation(summary = "Создать новую конфигурацию")
    public ResponseEntity<ConfigResponse> createConfiguration(@RequestBody ConfigurationCreateRequest request) {
        PCConfiguration config = configuratorService.createConfiguration(request.getUserId());
        config.setName(request.getName());
        config.setDescription(request.getDescription());
        // Сохраняем обновленную конфигурацию
        config = configuratorService.updateConfiguration(config);
        return new ResponseEntity<>(mapToConfigResponse(config), HttpStatus.CREATED);
    }

    @GetMapping("/{configId}")
    @Operation(summary = "Получить конфигурацию по ID")
    public ResponseEntity<ConfigResponse> getConfiguration(@PathVariable Long configId) {
        PCConfiguration config = configuratorService.getConfiguration(configId);
        return ResponseEntity.ok(mapToConfigResponse(config));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить все конфигурации пользователя")
    public ResponseEntity<List<ConfigResponse>> getUserConfigurations(@PathVariable Long userId) {
        List<PCConfiguration> configs = configuratorService.getUserConfigurations(userId);
        List<ConfigResponse> response = configs.stream()
                .map(this::mapToConfigResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{configId}/components")
    @Operation(summary = "Добавить компонент в конфигурацию")
    public ResponseEntity<ConfigResponse> addComponent(
            @PathVariable Long configId,
            @RequestBody ComponentAddRequest request) {
        PCConfiguration config = configuratorService.addComponent(configId, request.getProductId());
        return ResponseEntity.ok(mapToConfigResponse(config));
    }

    @DeleteMapping("/{configId}/components/{productId}")
    @Operation(summary = "Удалить компонент из конфигурации")
    public ResponseEntity<ConfigResponse> removeComponent(
            @PathVariable Long configId,
            @PathVariable Long productId) {
        PCConfiguration config = configuratorService.removeComponent(configId, productId);
        return ResponseEntity.ok(mapToConfigResponse(config));
    }

    @GetMapping("/{configId}/compatible-components")
    @Operation(summary = "Получить совместимые компоненты для конфигурации")
    public ResponseEntity<List<Product>> getCompatibleComponents(
            @PathVariable Long configId,
            @RequestParam ComponentType type) {
        List<Product> compatibleProducts = configuratorService.getCompatibleComponents(configId, type);
        return ResponseEntity.ok(compatibleProducts);
    }

    @GetMapping("/price-range")
    @Operation(summary = "Получить конфигурации в диапазоне цен")
    public ResponseEntity<List<ConfigResponse>> getConfigurationsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<PCConfiguration> configs = configuratorService.getConfigurationsByPriceRange(minPrice, maxPrice);
        List<ConfigResponse> response = configs.stream()
                .map(this::mapToConfigResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-performance")
    @Operation(summary = "Получить конфигурации по минимальной производительности")
    public ResponseEntity<List<ConfigResponse>> getConfigurationsByPerformance(
            @RequestParam Double minPerformance) {
        List<PCConfiguration> configs = configuratorService.getConfigurationsByPerformance(minPerformance);
        List<ConfigResponse> response = configs.stream()
                .map(this::mapToConfigResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "Удалить конфигурацию")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long configId) {
        configuratorService.deleteConfiguration(configId);
        return ResponseEntity.noContent().build();
    }

    private ConfigResponse mapToConfigResponse(PCConfiguration config) {
        return ConfigResponse.builder()
                .id(config.getId())
                .userId(config.getUser().getId())
                .name(config.getName())
                .description(config.getDescription())
                .totalPrice(config.getTotalPrice())
                .totalPerformance(config.getTotalPerformance())
                .isCompatible(config.getIsCompatible())
                .components(config.getComponents().stream()
                        .map(component -> Map.of(
                                "productId", component.getProduct().getId(),
                                "productName", component.getProduct().getTitle(),
                                "type", component.getProduct().getComponentType(),
                                "price", component.getProduct().getPrice()
                        ))
                        .collect(Collectors.toList()))
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
} 