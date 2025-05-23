package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.PCConfiguratorService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Validated
public class PCConfiguratorController {

    private final PCConfiguratorService configuratorService;

    @PostMapping
    public ResponseEntity<PCConfiguration> createConfiguration(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(configuratorService.createConfiguration(userId, name, description));
    }

    @GetMapping("/{configId}")
    public ResponseEntity<PCConfiguration> getConfiguration(@PathVariable @NotNull Long configId) {
        return configuratorService.getConfiguration(configId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PCConfiguration>> getUserConfigurations(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(configuratorService.getUserConfigurations(userId));
    }

    @PutMapping("/{configId}")
    public ResponseEntity<PCConfiguration> updateConfiguration(
            @PathVariable @NotNull Long configId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(configuratorService.updateConfiguration(configId, name, description));
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable @NotNull Long configId) {
        configuratorService.deleteConfiguration(configId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{configId}/components/{productId}")
    public ResponseEntity<PCConfiguration> addComponent(
            @PathVariable @NotNull Long configId,
            @PathVariable @NotNull Long productId) {
        return ResponseEntity.ok(configuratorService.addComponent(configId, productId));
    }

    @DeleteMapping("/{configId}/components/{productId}")
    public ResponseEntity<PCConfiguration> removeComponent(
            @PathVariable @NotNull Long configId,
            @PathVariable @NotNull Long productId) {
        return ResponseEntity.ok(configuratorService.removeComponent(configId, productId));
    }

    @GetMapping("/{configId}/compatibility")
    public ResponseEntity<Boolean> checkCompatibility(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.checkCompatibility(configId));
    }

    @GetMapping("/{configId}/compatibility-issues")
    public ResponseEntity<List<String>> getCompatibilityIssues(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getCompatibilityIssues(configId));
    }

    @GetMapping("/{configId}/compatible-components")
    public ResponseEntity<List<Product>> getCompatibleComponents(
            @PathVariable @NotNull Long configId,
            @RequestParam @NotNull ComponentType type) {
        return ResponseEntity.ok(configuratorService.getCompatibleComponents(configId, type));
    }

    @GetMapping("/{configId}/power-requirement")
    public ResponseEntity<Integer> getPowerRequirement(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.calculatePowerRequirement(configId));
    }

    @GetMapping("/{configId}/performance-score")
    public ResponseEntity<Double> getPerformanceScore(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.calculatePerformanceScore(configId));
    }

    @GetMapping("/{configId}/specs")
    public ResponseEntity<Map<String, Object>> getConfigurationSpecs(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getConfigurationSpecs(configId));
    }

    @GetMapping("/{configId}/recommendations")
    public ResponseEntity<Map<ComponentType, List<Product>>> getRecommendedComponents(
            @PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getRecommendedComponents(configId));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<PCConfiguration>> getRecommendedConfigurations(
            @RequestParam @NotBlank String purpose,
            @RequestParam @NotNull @Positive double budget) {
        return ResponseEntity.ok(configuratorService.getRecommendedConfigurations(purpose, budget));
    }

    @GetMapping("/{configId}/export")
    public ResponseEntity<String> exportConfiguration(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.exportConfiguration(configId));
    }

    @PostMapping("/import")
    public ResponseEntity<PCConfiguration> importConfiguration(
            @RequestParam @NotNull Long userId,
            @RequestBody @NotBlank String jsonConfig) {
        return ResponseEntity.ok(configuratorService.importConfiguration(userId, jsonConfig));
    }

    @PostMapping("/{configId}/clone")
    public ResponseEntity<PCConfiguration> cloneConfiguration(
            @PathVariable @NotNull Long configId,
            @RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(configuratorService.cloneConfiguration(configId, userId));
    }

    @GetMapping("/{configId}/availability")
    public ResponseEntity<Boolean> checkComponentsAvailability(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.checkComponentsAvailability(configId));
    }

    @GetMapping("/{configId}/missing-components")
    public ResponseEntity<List<Product>> getMissingComponents(@PathVariable @NotNull Long configId) {
        return ResponseEntity.ok(configuratorService.getMissingComponents(configId));
    }
} 