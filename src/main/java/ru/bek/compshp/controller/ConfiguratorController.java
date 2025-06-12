package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.ConfigComponentDto;
import ru.bek.compshp.dto.PCConfigurationDto;
import ru.bek.compshp.dto.ConfigurationWithComponentsRequest;
import ru.bek.compshp.model.PCConfiguration;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.service.ConfiguratorService;
import ru.bek.compshp.service.ConfiguratorServiceFixed;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Конфигуратор ПК", description = "API для управления конфигурациями ПК")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;
    private final ConfiguratorServiceFixed configuratorServiceFixed;

    @PostMapping
    @Operation(summary = "Создать новую конфигурацию")
    public ResponseEntity<PCConfigurationDto> createConfiguration(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category) {
        PCConfiguration config = configuratorService.createConfiguration(userId, name, description, category);
        return new ResponseEntity<>(mapToConfigurationDto(config), HttpStatus.CREATED);
    }

    @GetMapping("/{configId}")
    @Operation(summary = "Получить конфигурацию по ID")
    public ResponseEntity<PCConfigurationDto> getConfiguration(@PathVariable Long configId) {
        return configuratorService.getOptionalConfiguration(configId)
            .map(config -> ResponseEntity.ok(mapToConfigurationDto(config)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить все конфигурации пользователя")
    public ResponseEntity<List<PCConfigurationDto>> getUserConfigurations(@PathVariable Long userId) {
        List<PCConfiguration> configs = configuratorService.getUserConfigurations(userId);
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{configId}")
    @Operation(summary = "Обновить информацию о конфигурации")
    public ResponseEntity<PCConfigurationDto> updateConfiguration(
            @PathVariable @NotNull Long configId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category) {
        PCConfiguration config = configuratorService.updateConfiguration(configId, name, description, category);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    @PostMapping("/{configId}/components/{productId}")
    @Operation(summary = "Добавить компонент в конфигурацию")
    public ResponseEntity<PCConfigurationDto> addComponent(
            @PathVariable Long configId,
            @PathVariable Long productId) {
        PCConfiguration config = configuratorService.addComponent(configId, productId);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    @DeleteMapping("/{configId}/components/{productId}")
    @Operation(summary = "Удалить компонент из конфигурации")
    public ResponseEntity<PCConfigurationDto> removeComponent(
            @PathVariable Long configId,
            @PathVariable Long productId) {
        PCConfiguration config = configuratorService.removeComponent(configId, productId);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }
    
    @GetMapping("/{configId}/compatibility")
    @Operation(summary = "Проверить совместимость компонентов в конфигурации")
    public ResponseEntity<Boolean> checkCompatibility(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.checkCompatibility(configId));
    }

    @GetMapping("/{configId}/compatibility-issues")
    @Operation(summary = "Получить список проблем совместимости")
    public ResponseEntity<List<String>> getCompatibilityIssues(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getCompatibilityIssues(configId));
    }

    @GetMapping("/{configId}/compatible-components")
    @Operation(summary = "Получить совместимые компоненты для конфигурации")
    public ResponseEntity<List<Product>> getCompatibleComponents(
            @PathVariable Long configId,
            @RequestParam ComponentType type) {
        List<Product> compatibleProducts = configuratorService.getCompatibleComponents(configId, type);
        return ResponseEntity.ok(compatibleProducts);
    }
    
    @GetMapping("/{configId}/power-requirement")
    @Operation(summary = "Рассчитать потребляемую мощность")
    public ResponseEntity<Integer> getPowerRequirement(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.calculatePowerRequirement(configId));
    }

    @GetMapping("/{configId}/performance-score")
    @Operation(summary = "Получить оценку производительности конфигурации")
    public ResponseEntity<Double> getPerformanceScore(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.calculatePerformanceScore(configId));
    }

    @GetMapping("/{configId}/specs")
    @Operation(summary = "Получить спецификации конфигурации")
    public ResponseEntity<Map<String, Object>> getConfigurationSpecs(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getConfigurationSpecs(configId));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Получить конфигурации в диапазоне цен")
    public ResponseEntity<List<PCConfigurationDto>> getConfigurationsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<PCConfiguration> configs = configuratorService.getConfigurationsByPriceRange(minPrice, maxPrice);
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-performance")
    @Operation(summary = "Получить конфигурации по минимальной производительности")
    public ResponseEntity<List<PCConfigurationDto>> getConfigurationsByPerformance(
            @RequestParam Double minPerformance) {
        List<PCConfiguration> configs = configuratorService.getConfigurationsByPerformance(minPerformance);
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{configId}/recommendations")
    @Operation(summary = "Получить рекомендуемые компоненты для конфигурации")
    public ResponseEntity<Map<ComponentType, List<Product>>> getRecommendedComponents(
            @PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getRecommendedComponents(configId));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Получить рекомендуемые конфигурации по назначению и бюджету")
    public ResponseEntity<List<PCConfigurationDto>> getRecommendedConfigurations(
            @RequestParam @NotBlank String purpose,
            @RequestParam @NotNull @Positive double budget) {
        List<PCConfiguration> configs = configuratorService.getRecommendedConfigurations(purpose, budget);
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{configId}/export")
    @Operation(summary = "Экспортировать конфигурацию")
    public ResponseEntity<String> exportConfiguration(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.exportConfiguration(configId));
    }

    @PostMapping("/import")
    @Operation(summary = "Импортировать конфигурацию")
    public ResponseEntity<PCConfigurationDto> importConfiguration(
            @RequestParam @NotNull Long userId,
            @RequestBody @NotBlank String jsonConfig) {
        PCConfiguration config = configuratorService.importConfiguration(userId, jsonConfig);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    @PostMapping("/{configId}/clone")
    @Operation(summary = "Клонировать конфигурацию")
    public ResponseEntity<PCConfigurationDto> cloneConfiguration(
            @PathVariable @NotNull Long configId,
            @RequestParam @NotNull Long userId) {
        PCConfiguration config = configuratorService.cloneConfiguration(configId, userId);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    @GetMapping("/{configId}/availability")
    @Operation(summary = "Проверить наличие компонентов")
    public ResponseEntity<Boolean> checkComponentsAvailability(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.checkComponentsAvailability(configId));
    }

    @GetMapping("/{configId}/missing-components")
    @Operation(summary = "Получить список отсутствующих компонентов")
    public ResponseEntity<List<Product>> getMissingComponents(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getMissingComponents(configId));
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "Удалить конфигурацию")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long configId) {
        configuratorService.deleteConfiguration(configId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{configId}/load")
    @Operation(summary = "Загрузить сохраненную конфигурацию в конфигуратор")
    public ResponseEntity<PCConfigurationDto> loadSavedConfiguration(@PathVariable @NotNull Long configId) {
        PCConfiguration config = configuratorService.loadSavedConfiguration(configId);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    @GetMapping("/{configId}/products")
    @Operation(summary = "Получить продукты из сохраненной конфигурации для конфигуратора")
    public ResponseEntity<Map<String, Product>> getConfigurationProducts(@PathVariable @NotNull Long configId) {
        Map<ComponentType, Product> products = configuratorService.getConfigurationProducts(configId);
        
        // Преобразуем ключи из enum в строки для более удобного использования на фронтенде
        Map<String, Product> result = new HashMap<>();
        products.forEach((type, product) -> result.put(type.name(), product));
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/with-components")
    @Operation(summary = "Создать новую конфигурацию с компонентами")
    public ResponseEntity<PCConfigurationDto> createConfigurationWithComponents(
            @RequestBody ConfigurationWithComponentsRequest request) {
        PCConfiguration config = configuratorServiceFixed.createConfigurationWithComponents(
            request.getUserId(),
            request.getName(),
            request.getDescription(),
            request.getCategory(),
            request.getComponentIds()
        );
        return new ResponseEntity<>(mapToConfigurationDto(config), HttpStatus.CREATED);
    }

    @GetMapping("/public")
    @Operation(summary = "Получить публичные конфигурации")
    public ResponseEntity<List<PCConfigurationDto>> getPublicConfigurations() {
        List<PCConfiguration> configs = configuratorService.getPublicConfigurations();
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{configId}/toggle-publication")
    @Operation(summary = "Переключить статус публикации конфигурации")
    public ResponseEntity<PCConfigurationDto> toggleConfigurationPublication(@PathVariable @NotNull Long configId) {
        PCConfiguration config = configuratorService.toggleConfigurationPublication(configId);
        return ResponseEntity.ok(mapToConfigurationDto(config));
    }

    private PCConfigurationDto mapToConfigurationDto(PCConfiguration config) {
        PCConfigurationDto dto = PCConfigurationDto.builder()
                .id(config.getId())
                .userId(config.getUser().getId())
                .name(config.getName())
                .description(config.getDescription())
                .category(config.getCategory())
                .totalPrice(config.getTotalPrice())
                .totalPerformance(config.getTotalPerformance())
                .isCompatible(config.getIsCompatible())
                .isPublic(config.getIsPublic())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
        
        // Преобразуем компоненты конфигурации в DTO с полной информацией о продуктах
        List<ConfigComponentDto> componentDtos = config.getComponents().stream()
                .map(component -> {
                    Product product = component.getProduct();
                    ConfigComponentDto componentDto = ConfigComponentDto.builder()
                            .id(component.getId().getProductId())
                            .productId(product.getId())
                            .productName(product.getTitle())
                            .type(product.getComponentType())
                            .price(product.getPrice())
                            .manufacturerName(product.getManufacturer() != null ? product.getManufacturer().getName() : "")
                            .manufacturerId(product.getManufacturer() != null ? product.getManufacturer().getId() : null)
                            .build();
                    
                    // Добавляем спецификации продукта
                    if (product.getSpecs() != null) {
                        componentDto.setSpecs(product.getSpecs());
                    }
                    
                    return componentDto;
                })
                .collect(Collectors.toList());
        
        dto.setComponents(componentDtos);
        
        // Устанавливаем ID компонентов по типам для совместимости с существующим кодом
        for (ConfigComponentDto component : componentDtos) {
            if (component.getType() == null) continue;
            
            switch (component.getType()) {
                case CPU:
                    dto.setCpuId(component.getProductId());
                    break;
                case MB:
                    dto.setMotherboardId(component.getProductId());
                    break;
                case GPU:
                    dto.setGpuId(component.getProductId());
                    break;
                case PSU:
                    dto.setPsuId(component.getProductId());
                    break;
                case CASE:
                    dto.setCaseId(component.getProductId());
                    break;
                case RAM:
                    if (dto.getRamIds() == null) {
                        dto.setRamIds(new HashSet<>());
                    }
                    dto.getRamIds().add(component.getProductId());
                    break;
                case STORAGE:
                    if (dto.getStorageIds() == null) {
                        dto.setStorageIds(new HashSet<>());
                    }
                    dto.getStorageIds().add(component.getProductId());
                    break;
                default:
                    break;
            }
        }
        
        return dto;
    }
} 