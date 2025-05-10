package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.*;
import ru.compshp.dto.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configurator")
public class PCConfiguratorController {
    private final PCConfigurationService configurationService;
    private final ComponentCompatibilityService compatibilityService;
    private final ProductService productService;

    public PCConfiguratorController(
            PCConfigurationService configurationService,
            ComponentCompatibilityService compatibilityService,
            ProductService productService) {
        this.configurationService = configurationService;
        this.compatibilityService = compatibilityService;
        this.productService = productService;
    }

    @PostMapping("/start")
    public ResponseEntity<PCConfiguration> startConfiguration(@RequestBody ConfigurationRequest request) {
        PCConfiguration config = configurationService.createConfiguration(
            request.getName(),
            request.getDescription()
        );
        return ResponseEntity.ok(config);
    }

    @PostMapping("/{configId}/add-component")
    public ResponseEntity<PCConfiguration> addComponent(
            @PathVariable Long configId,
            @RequestBody AddComponentRequest request) {
        PCConfiguration config = configurationService.addComponent(configId, request.getProductId());
        return ResponseEntity.ok(config);
    }

    @GetMapping("/{configId}/compatible-components")
    public ResponseEntity<Map<ComponentType, List<Product>>> getCompatibleComponents(
            @PathVariable Long configId,
            @RequestParam ComponentType baseType) {
        Product baseComponent = configurationService.getById(configId)
                .getComponents().stream()
                .filter(c -> c.getProduct().getComponentType() == baseType)
                .findFirst()
                .map(ConfigComponent::getProduct)
                .orElseThrow(() -> new RuntimeException("Base component not found"));

        Map<ComponentType, List<Product>> compatibleComponents = 
            compatibilityService.getCompatibleComponentsByType(baseComponent);
        return ResponseEntity.ok(compatibleComponents);
    }

    @GetMapping("/{configId}/recommendations")
    public ResponseEntity<List<Product>> getRecommendations(
            @PathVariable Long configId,
            @RequestParam ComponentType targetType) {
        PCConfiguration config = configurationService.getById(configId);
        List<Product> selectedComponents = config.getComponents().stream()
                .map(ConfigComponent::getProduct)
                .toList();

        List<Product> recommendations = compatibilityService.getRecommendedComponents(
            selectedComponents,
            targetType
        );
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{configId}/compatibility-check")
    public ResponseEntity<CompatibilityCheckResponse> checkCompatibility(@PathVariable Long configId) {
        PCConfiguration config = configurationService.getById(configId);
        List<Product> components = config.getComponents().stream()
                .map(ConfigComponent::getProduct)
                .toList();

        boolean isCompatible = compatibilityService.checkConfigurationCompatibility(
            components.get(0),
            components.subList(1, components.size())
        );

        CompatibilityCheckResponse response = new CompatibilityCheckResponse(
            isCompatible,
            config.getComponents().size() == ComponentType.values().length
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{configId}/export")
    public ResponseEntity<String> exportConfiguration(@PathVariable Long configId) {
        String export = configurationService.exportConfiguration(configId);
        return ResponseEntity.ok(export);
    }
} 