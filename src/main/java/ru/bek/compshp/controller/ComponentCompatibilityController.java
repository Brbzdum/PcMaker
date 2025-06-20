package ru.bek.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.CompatibilityAnalysisResult;
import ru.bek.compshp.dto.CompatibilityRuleDto;
import ru.bek.compshp.dto.ProductDto;
import ru.bek.compshp.mapper.CompatibilityRuleMapper;
import ru.bek.compshp.mapper.ProductMapper;
import ru.bek.compshp.model.CompatibilityRule;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.service.ComponentCompatibilityService;
import ru.bek.compshp.service.CompatibilityAnalysisService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compatibility")
@RequiredArgsConstructor
public class ComponentCompatibilityController {
    private final ComponentCompatibilityService compatibilityService;
    private final CompatibilityAnalysisService analysisService;
    private final ProductMapper productMapper;
    private final CompatibilityRuleMapper ruleMapper;

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

    /**
     * Выполняет детальный анализ совместимости конфигурации
     * @param configId ID конфигурации
     * @return детальный результат анализа
     */
    @PostMapping("/analyze-configuration/{configId}")
    public ResponseEntity<CompatibilityAnalysisResult> analyzeConfiguration(
            @PathVariable Long configId) {
        CompatibilityAnalysisResult result = analysisService.analyzeConfiguration(configId);
        return ResponseEntity.ok(result);
    }

    /**
     * Выполняет детальный анализ совместимости списка компонентов
     * @param componentIds список ID компонентов
     * @return детальный результат анализа
     */
    @PostMapping("/analyze-components")
    public ResponseEntity<CompatibilityAnalysisResult> analyzeComponents(
            @RequestBody List<Long> componentIds) {
        List<Product> components = componentIds.stream()
                .map(compatibilityService::getProductById)
                .collect(Collectors.toList());
        
        CompatibilityAnalysisResult result = analysisService.analyzeConfiguration(components);
        return ResponseEntity.ok(result);
    }

    /**
     * Получает причину несовместимости двух компонентов
     * @param sourceId ID первого компонента
     * @param targetId ID второго компонента
     * @return причина несовместимости или null, если компоненты совместимы
     */
    @GetMapping("/incompatibility-reason")
    public ResponseEntity<String> getIncompatibilityReason(
            @RequestParam Long sourceId,
            @RequestParam Long targetId) {
        Product source = compatibilityService.getProductById(sourceId);
        Product target = compatibilityService.getProductById(targetId);
        String reason = compatibilityService.getIncompatibilityReason(source, target);
        return ResponseEntity.ok(reason);
    }

    @GetMapping("/compatible/{sourceId}")
    public ResponseEntity<List<ProductDto>> getCompatibleComponents(
            @PathVariable Long sourceId,
            @RequestParam ComponentType targetType) {
        Product source = compatibilityService.getProductById(sourceId);
        List<Product> compatibleProducts = compatibilityService.getCompatibleComponents(source, targetType);
        List<ProductDto> compatibleProductDtos = compatibleProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(compatibleProductDtos);
    }

    @PostMapping("/rules")
    public ResponseEntity<?> createRule(@RequestBody CompatibilityRuleDto ruleDto) {
        CompatibilityRule rule = ruleMapper.toEntity(ruleDto);
        List<String> conflicts = compatibilityService.checkRuleConflicts(rule);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("conflicts", conflicts));
        }
        CompatibilityRule savedRule = compatibilityService.saveCompatibilityRule(rule);
        return ResponseEntity.ok(ruleMapper.toDto(savedRule));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<?> updateRule(
            @PathVariable Long id,
            @RequestBody CompatibilityRuleDto ruleDto) {
        ruleDto.setId(id);
        CompatibilityRule rule = ruleMapper.toEntity(ruleDto);
        List<String> conflicts = compatibilityService.checkRuleConflicts(rule);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("conflicts", conflicts));
        }
        CompatibilityRule savedRule = compatibilityService.saveCompatibilityRule(rule);
        return ResponseEntity.ok(ruleMapper.toDto(savedRule));
    }

    @GetMapping("/rules/{componentType}")
    public ResponseEntity<List<CompatibilityRuleDto>> getRulesForComponentType(
            @PathVariable ComponentType componentType) {
        List<CompatibilityRule> rules = compatibilityService.getRulesForComponentType(componentType);
        List<CompatibilityRuleDto> ruleDtos = ruleMapper.toDtoList(rules);
        return ResponseEntity.ok(ruleDtos);
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