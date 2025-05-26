package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.CategoryDTO;
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
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryByName(name));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
        } catch (DuplicateCategoryException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
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
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getSubcategories(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/{id}/path")
    public ResponseEntity<List<CategoryDTO>> getCategoryPath(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryPath(id));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 