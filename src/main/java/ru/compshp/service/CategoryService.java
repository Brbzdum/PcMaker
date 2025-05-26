package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.dto.CategoryDTO;
import ru.compshp.exception.CategoryNotFoundException;
import ru.compshp.exception.DuplicateCategoryException;
import ru.compshp.mapper.CategoryMapper;
import ru.compshp.model.Category;
import ru.compshp.model.Product;
import ru.compshp.repository.CategoryRepository;
import ru.compshp.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    private final CategoryMapper categoryMapper;

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Cacheable(value = "categories", key = "'name:' + #name")
    public CategoryDTO getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
    }

    @Cacheable(value = "categories", key = "'subcategories:' + #parentId")
    public List<CategoryDTO> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'root'")
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDTO.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryDTO);
        
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getName().equals(categoryDTO.getName()) &&
            categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDTO.getName() + " already exists");
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            existingCategory.setParent(parent);
        } else {
            existingCategory.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }

        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        }

        categoryRepository.delete(category);
    }

    @Cacheable(value = "categories", key = "'path:' + #categoryId")
    public List<CategoryDTO> getCategoryPath(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        return category.getParent() != null ?
                getCategoryPath(category.getParent().getId()).stream()
                        .collect(Collectors.toList()) :
                List.of(categoryMapper.toDTO(category));
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }
} 