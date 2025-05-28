package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.ComponentCompatibilityService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compatibility")
@RequiredArgsConstructor
public class ComponentCompatibilityController {
    private final ComponentCompatibilityService compatibilityService;

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkCompatibility(
            @RequestParam Long sourceId,
            @RequestParam Long targetId) {
        Product source = compatibilityService.getProductById(sourceId);
        Product target = compatibilityService.getProductById(targetId);
        return ResponseEntity.ok(compatibilityService.checkComponentsCompatibility(source, target));
    }

    @PostMapping("/check-configuration")
    public ResponseEntity<Boolean> checkConfigurationCompatibility(
            @RequestParam Long newComponentId,
            @RequestBody List<Long> existingComponentIds) {
        Product newComponent = compatibilityService.getProductById(newComponentId);
        List<Product> existingComponents = existingComponentIds.stream()
                .map(compatibilityService::getProductById)
                .toList();
        return ResponseEntity.ok(compatibilityService.checkConfigurationCompatibility(newComponent, existingComponents));
    }

    @GetMapping("/compatible/{sourceId}")
    public ResponseEntity<List<Product>> getCompatibleComponents(
            @PathVariable Long sourceId,
            @RequestParam ComponentType targetType) {
        Product source = compatibilityService.getProductById(sourceId);
        return ResponseEntity.ok(compatibilityService.getCompatibleComponents(source, targetType));
    }

    @PostMapping("/rules")
    public ResponseEntity<?> createRule(@RequestBody CompatibilityRule rule) {
        List<String> conflicts = compatibilityService.checkRuleConflicts(rule);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("conflicts", conflicts));
        }
        return ResponseEntity.ok(compatibilityService.saveCompatibilityRule(rule));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<?> updateRule(
            @PathVariable Long id,
            @RequestBody CompatibilityRule rule) {
        rule.setId(id);
        List<String> conflicts = compatibilityService.checkRuleConflicts(rule);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("conflicts", conflicts));
        }
        return ResponseEntity.ok(compatibilityService.saveCompatibilityRule(rule));
    }

    @GetMapping("/rules/{componentType}")
    public ResponseEntity<List<CompatibilityRule>> getRulesForComponentType(
            @PathVariable ComponentType componentType) {
        return ResponseEntity.ok(compatibilityService.getRulesForComponentType(componentType));
    }

    @PostMapping("/update-compatibility")
    public ResponseEntity<Void> updateProductCompatibility(
            @RequestParam Long sourceId,
            @RequestParam Long targetId) {
        Product source = compatibilityService.getProductById(sourceId);
        Product target = compatibilityService.getProductById(targetId);
        compatibilityService.updateProductCompatibility(source, target);
        return ResponseEntity.ok().build();
    }
} 