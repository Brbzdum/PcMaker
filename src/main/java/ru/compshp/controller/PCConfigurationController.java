package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.PCConfigurationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configurations")
public class PCConfigurationController {
    private final PCConfigurationService configurationService;

    public PCConfigurationController(PCConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public ResponseEntity<List<PCConfiguration>> getAllConfigurations() {
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PCConfiguration> getConfiguration(@PathVariable Long id) {
        return configurationService.getConfiguration(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PCConfiguration> createConfiguration(
            @RequestParam String name,
            @RequestParam String description) {
        return ResponseEntity.ok(configurationService.createConfiguration(name, description));
    }

    @PostMapping("/{id}/components")
    public ResponseEntity<PCConfiguration> addComponent(
            @PathVariable Long id,
            @RequestParam Long productId) {
        try {
            return ResponseEntity.ok(configurationService.addComponent(id, productId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/components/{componentId}")
    public ResponseEntity<PCConfiguration> removeComponent(
            @PathVariable Long id,
            @PathVariable Long componentId) {
        try {
            return ResponseEntity.ok(configurationService.removeComponent(id, componentId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/components/{componentId}")
    public ResponseEntity<PCConfiguration> replaceComponent(
            @PathVariable Long id,
            @PathVariable Long componentId,
            @RequestParam Long newProductId) {
        try {
            return ResponseEntity.ok(configurationService.replaceComponent(id, componentId, newProductId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<PCConfiguration> cloneConfiguration(
            @PathVariable Long id,
            @RequestParam String newName) {
        try {
            return ResponseEntity.ok(configurationService.cloneConfiguration(id, newName));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<Map<String, Object>> getConfigurationPrice(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
            "totalPrice", configurationService.calculateTotalPrice(id),
            "isAvailable", configurationService.checkComponentsAvailability(id)
        ));
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<Product>> getRecommendedComponents(
            @PathVariable Long id,
            @RequestParam ComponentType type) {
        return ResponseEntity.ok(configurationService.getRecommendedComponents(id, type));
    }

    @GetMapping("/{id}/power")
    public ResponseEntity<Map<String, Integer>> getPowerConsumption(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
            "powerConsumption", configurationService.calculatePowerConsumption(id)
        ));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<String> exportConfiguration(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(configurationService.exportConfiguration(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 