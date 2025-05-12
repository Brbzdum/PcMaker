package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Category;
import ru.compshp.model.Product;
import ru.compshp.repository.CategoryRepository;
import ru.compshp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Сервис для управления категориями
 * Основные функции:
 * - Управление категориями (CRUD)
 * - Получение продуктов по категории
 * - Управление иерархией категорий
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .map(productRepository::findByCategory)
            .orElse(List.of());
    }

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        return categoryRepository.findById(id)
            .map(existingCategory -> {
                existingCategory.setName(category.getName());
                existingCategory.setDescription(category.getDescription());
                existingCategory.setParent(category.getParent());
                return categoryRepository.save(existingCategory);
            })
            .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public void deleteCategory(Long id) {
        // Проверяем, есть ли подкатегории
        if (!categoryRepository.findByParentId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
        
        // Проверяем, есть ли продукты в категории
        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete category with products");
        }

        categoryRepository.deleteById(id);
    }

    public List<Category> getMainCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> getCategoryPath(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .map(category -> {
                List<Category> path = new ArrayList<>();
                Category current = category;
                while (current != null) {
                    path.add(0, current);
                    current = current.getParent();
                }
                return path;
            })
            .orElse(List.of());
    }
} 