package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.ConfigComponentDto;
import ru.bek.compshp.dto.PCConfigurationDto;
import ru.bek.compshp.model.ConfigComponent;
import ru.bek.compshp.model.PCConfiguration;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.security.CustomUserDetails;
import ru.bek.compshp.service.ConfiguratorService;
import ru.bek.compshp.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashSet;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Конфигуратор ПК", description = "API для управления конфигурациями ПК")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;

    @PostMapping
    @Operation(summary = "Создать новую конфигурацию")
    public ResponseEntity<PCConfigurationDto> createConfiguration(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description) {
        PCConfiguration config = configuratorService.createConfiguration(userId, name, description);
        return new ResponseEntity<>(mapToConfigurationDto(config), HttpStatus.CREATED);
    }

    @PostMapping("/with-components")
    @Operation(summary = "Создать новую конфигурацию с компонентами")
    public ResponseEntity<PCConfigurationDto> createConfigurationWithComponents(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestBody List<Long> componentIds) {
        PCConfiguration config = configuratorService.createConfiguration(userId, name, description);
        
        // Добавляем компоненты в конфигурацию
        for (Long componentId : componentIds) {
            configuratorService.addComponent(config.getId(), componentId);
        }
        
        // Обновляем конфигурацию после добавления всех компонентов
        config = configuratorService.getConfigurationWithComponents(config.getId());
        
        return new ResponseEntity<>(mapToConfigurationDto(config), HttpStatus.CREATED);
    }

    @GetMapping("/{configId}")
    @Operation(summary = "Получить конфигурацию по ID")
    public ResponseEntity<PCConfigurationDto> getConfiguration(@PathVariable Long configId) {
        try {
            PCConfiguration config = configuratorService.getConfigurationWithComponents(configId);
            return ResponseEntity.ok(mapToConfigurationDto(config));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить все конфигурации пользователя")
    public ResponseEntity<List<PCConfigurationDto>> getUserConfigurations(@PathVariable Long userId) {
        List<PCConfiguration> configs = configuratorService.getUserConfigurationsWithComponents(userId);
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
            @RequestParam(required = false) String description) {
        PCConfiguration config = configuratorService.updateConfiguration(configId, name, description);
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

    @PostMapping("/create")
    @Operation(summary = "Создать новую конфигурацию с параметрами в теле запроса")
    public ResponseEntity<PCConfigurationDto> createConfigurationFromBody(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("Received configuration creation request: " + requestBody);
            
            // Получаем имя конфигурации
            String name = (String) requestBody.get("name");
            if (name == null || name.isEmpty()) {
                System.out.println("Error: Name is empty");
                return ResponseEntity.badRequest().build();
            }
            
            // Получаем ID пользователя из токена аутентификации
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                System.out.println("Error: User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            System.out.println("User ID from token: " + userId);
            
            // Получаем описание, если оно есть
            String description = (String) requestBody.get("description");
            
            // Создаем конфигурацию
            PCConfiguration config = configuratorService.createConfiguration(userId, name, description);
            System.out.println("Created base configuration with ID: " + config.getId());
            
            // Если есть компоненты, добавляем их
            if (requestBody.containsKey("componentIds")) {
                List<Integer> componentIds = (List<Integer>) requestBody.get("componentIds");
                System.out.println("Adding components: " + componentIds);
                
                for (Integer componentId : componentIds) {
                    try {
                        configuratorService.addComponent(config.getId(), componentId.longValue());
                        System.out.println("Added component " + componentId + " to configuration " + config.getId());
                    } catch (Exception e) {
                        System.out.println("Error adding component " + componentId + ": " + e.getMessage());
                    }
                }
                
                // Обновляем конфигурацию после добавления всех компонентов
                config = configuratorService.getConfigurationWithComponents(config.getId());
                System.out.println("Final configuration: " + config);
                System.out.println("Components count: " + (config.getComponents() != null ? config.getComponents().size() : "null"));
            }
            
            PCConfigurationDto result = mapToConfigurationDto(config);
            System.out.println("Returning DTO with components: " + (result.getComponents() != null ? result.getComponents().size() : "null"));
            
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error creating configuration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/public")
    @Operation(summary = "Получить публичные конфигурации")
    public ResponseEntity<List<PCConfigurationDto>> getPublicConfigurations() {
        List<PCConfiguration> configs = configuratorService.getPublicConfigurationsWithComponents();
        List<PCConfigurationDto> response = configs.stream()
                .map(this::mapToConfigurationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private PCConfigurationDto mapToConfigurationDto(PCConfiguration config) {
        return PCConfigurationDto.builder()
                .id(config.getId())
                .userId(config.getUser().getId())
                .name(config.getName())
                .description(config.getDescription())
                .totalPrice(config.getTotalPrice())
                .totalPerformance(config.getTotalPerformance())
                .isCompatible(config.getIsCompatible())
                .components(config.getComponents().stream()
                        .map(component -> ConfigComponentDto.builder()
                                .id(component.getId().getProductId())
                                .productId(component.getProduct().getId())
                                .productName(component.getProduct().getTitle())
                                .type(component.getProduct().getComponentType())
                                .price(component.getProduct().getPrice())
                                .quantity(component.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
} 