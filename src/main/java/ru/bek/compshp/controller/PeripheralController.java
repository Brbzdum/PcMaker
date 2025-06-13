package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.repository.ProductRepository;
import ru.bek.compshp.repository.CategoryRepository;
import ru.bek.compshp.util.PeripheralTypeMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с периферийными устройствами
 */
@RestController
@RequestMapping("/api/peripherals")
@RequiredArgsConstructor
@Tag(name = "Периферия", description = "API для работы с периферийными устройствами")
public class PeripheralController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PeripheralTypeMapper peripheralTypeMapper;

    @GetMapping
    @Operation(summary = "Получить список всей периферии")
    public ResponseEntity<List<Product>> getAllPeripherals() {
        return ResponseEntity.ok(productRepository.findAllPeripherals());
    }

    @GetMapping("/paged")
    @Operation(summary = "Получить список всей периферии с пагинацией")
    public ResponseEntity<Page<Product>> getAllPeripheralsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(productRepository.findAllPeripherals(pageable));
    }

    @GetMapping("/category/{slug}")
    @Operation(summary = "Получить периферию по категории")
    public ResponseEntity<List<Product>> getPeripheralsByCategory(@PathVariable String slug) {
        Optional<Category> categoryOpt = categoryRepository.findBySlug(slug);
        if (categoryOpt.isPresent() && categoryOpt.get().getIsPeripheral()) {
            return ResponseEntity.ok(productRepository.findByCategoryAndCategoryIsPeripheralTrue(categoryOpt.get()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/budget")
    @Operation(summary = "Получить периферию в заданном бюджете")
    public ResponseEntity<List<Product>> getPeripheralsInBudget(
            @RequestParam String categorySlug,
            @RequestParam BigDecimal maxPrice) {
        
        Optional<Category> categoryOpt = categoryRepository.findBySlug(categorySlug);
        if (categoryOpt.isPresent() && categoryOpt.get().getIsPeripheral()) {
            return ResponseEntity.ok(productRepository.findPeripheralsInBudget(categoryOpt.get().getId(), maxPrice));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/types")
    @Operation(summary = "Получить список всех типов периферии")
    public ResponseEntity<List<String>> getPeripheralTypes() {
        // Получаем все периферийные категории и маппим их в типы периферии
        List<Category> peripheralCategories = categoryRepository.findByIsPeripheral(true);
        
        List<String> peripheralTypes = peripheralCategories.stream()
            .map(category -> {
                // Используем маппер для получения типа периферии по ID категории
                String mappedType = peripheralTypeMapper.getPeripheralTypeByCategoryId(category.getId());
                if (mappedType != null) {
                    return mappedType;
                }
                // Если маппинг не найден, используем slug категории в нижнем регистре
                if (category.getSlug() != null) {
                    return category.getSlug().toLowerCase();
                }
                return null;
            })
            .filter(type -> type != null && !type.isEmpty())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(peripheralTypes);
    }
    
    @GetMapping("/type-mapping")
    @Operation(summary = "Получить соответствие между ID категорий и типами периферии")
    public ResponseEntity<Map<Long, String>> getPeripheralTypeMapping() {
        // Получаем все периферийные категории
        List<Category> peripheralCategories = categoryRepository.findByIsPeripheral(true);
        
        // Создаем карту соответствий
        Map<Long, String> mapping = peripheralCategories.stream()
            .filter(category -> category.getId() != null)
            .collect(Collectors.toMap(
                Category::getId,
                category -> {
                    // Используем маппер для получения типа периферии по ID категории
                    String mappedType = peripheralTypeMapper.getPeripheralTypeByCategoryId(category.getId());
                    if (mappedType != null) {
                        return mappedType;
                    }
                    // Если маппинг не найден, используем slug категории в нижнем регистре
                    if (category.getSlug() != null) {
                        return category.getSlug().toLowerCase();
                    }
                    return "unknown";
                }
            ));
        
        return ResponseEntity.ok(mapping);
    }
} 