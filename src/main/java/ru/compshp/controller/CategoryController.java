package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.CategoryDto;
import ru.compshp.exception.CategoryNotFoundException;
import ru.compshp.exception.DuplicateCategoryException;
import ru.compshp.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryByName(name));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto CategoryDto) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(CategoryDto));
        } catch (DuplicateCategoryException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto CategoryDto) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, CategoryDto));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateCategoryException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<CategoryDto>> getSubcategories(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getSubcategories(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/pc-components")
    public ResponseEntity<List<CategoryDto>> getPcComponentCategories() {
        return ResponseEntity.ok(categoryService.getPcComponentCategories());
    }

    @GetMapping("/{id}/path")
    public ResponseEntity<List<CategoryDto>> getCategoryPath(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryPath(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 