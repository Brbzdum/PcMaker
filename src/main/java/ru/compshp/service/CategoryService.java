package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Category;
import ru.compshp.model.Product;
import ru.compshp.repository.CategoryRepository;
import ru.compshp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<Product> products = productRepository.findByCategoryEntity(category);
        if (!products.isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing products");
        }
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return productRepository.findByCategoryEntity(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        return categoryRepository.findByParentCategory(parent);
    }

    @Transactional
    public Category addSubcategory(Long parentId, String name, String description) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        Category subcategory = new Category();
        subcategory.setName(name);
        subcategory.setDescription(description);
        subcategory.setParentCategory(parent);
        return categoryRepository.save(subcategory);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoryPath(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<Category> path = new java.util.ArrayList<>();
        Category current = category;
        while (current != null) {
            path.add(0, current);
            current = current.getParentCategory();
        }
        return path;
    }
} 