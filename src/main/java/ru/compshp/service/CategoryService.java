package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.dto.CategoryDto;
import ru.compshp.exception.CategoryNotFoundException;
import ru.compshp.exception.DuplicateCategoryException;
import ru.compshp.mapper.CategoryMapper;
import ru.compshp.model.Category;
import ru.compshp.model.Product;
import ru.compshp.repository.CategoryRepository;
import ru.compshp.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
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
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Cacheable(value = "categories", key = "'name:' + #name")
    public CategoryDto getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with name: " + name));
    }

    @Cacheable(value = "categories", key = "'subcategories:' + #parentId")
    public List<CategoryDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'root'")
    public List<CategoryDto> getRootCategories() {
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
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDto.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        
        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getName().equals(categoryDto.getName()) &&
            categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new DuplicateCategoryException("Category with name " + categoryDto.getName() + " already exists");
        }

        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
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
    public List<CategoryDto> getCategoryPath(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        List<CategoryDto> result = new ArrayList<>();
        if (category.getParent() != null) {
            result.addAll(getCategoryPath(category.getParent().getId()));
        }
        result.add(categoryMapper.toDTO(category));
        return result;
    }

    /**
     * Проверяет, содержится ли категория с указанным ID в дереве категорий
     * @param category корневая категория для проверки
     * @param id ID искомой категории
     * @return true, если категория найдена
     */
    public boolean isPresent(Category category, Long id) {
        if (category == null) {
            return false;
        }
        if (category.getId().equals(id)) {
            return true;
        }
        for (Category child : category.getChildren()) {
            if (isPresent(child, id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет существование категории по ID
     * @param id ID категории
     * @return true, если категория существует
     */
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Проверяет существование категории по имени
     * @param name имя категории
     * @return true, если категория существует
     */
    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }

    /**
     * Выбрасывает исключение если категория не найдена
     * @param id ID категории
     * @throws CategoryNotFoundException если категория не найдена
     */
    public void orElseThrow(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
    }
} 