package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.ComponentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления продуктами
 * Основные функции:
 * - Управление продуктами (CRUD)
 * - Фильтрация и поиск продуктов
 * - Управление категориями
 * - Управление производителями
 * - Управление отзывами
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;

    // Basic CRUD operations
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Stock management
    public void updateStock(Long id, int quantity) {
        productRepository.findById(id).ifPresent(product -> {
            product.setStock(quantity);
            productRepository.save(product);
        });
    }

    public void decreaseStock(Long id, int quantity) {
        productRepository.findById(id).ifPresent(product -> {
            if (product.getStock() < quantity) {
                throw new RuntimeException("Not enough stock");
            }
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        });
    }

    // Product search and filtering
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByManufacturer(Long manufacturerId) {
        return productRepository.findByManufacturerId(manufacturerId);
    }

    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findDiscountedProducts();
    }

    // Configurator specific methods
    public List<Product> getCompatibleComponents(ComponentType type, Integer maxPower) {
        return productRepository.findCompatibleComponents(type, maxPower);
    }

    public List<Product> getComponentsInBudget(ComponentType type, BigDecimal maxPrice) {
        return productRepository.findComponentsInBudget(type, maxPrice);
    }

    public List<Product> getComponentsByMinPerformance(ComponentType type, Double minPerformance) {
        return productRepository.findComponentsByMinPerformance(type, minPerformance);
    }

    public List<Product> getReadyPCs(Long categoryId) {
        return productRepository.findReadyPCs(categoryId);
    }

    // Compatibility check
    public boolean isCompatible(Product component1, Product component2) {
        if (component1 == null || component2 == null) {
            return false;
        }
        return component1.isCompatibleWith(component2);
    }

    // Category management
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findChildCategories(parentId);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Manufacturer management
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Manufacturer saveManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }
} 