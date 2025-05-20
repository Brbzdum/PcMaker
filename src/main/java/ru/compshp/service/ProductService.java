package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ReviewRepository reviewRepository;

    // Product methods
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByManufacturer(Long manufacturerId) {
        return productRepository.findByManufacturerId(manufacturerId);
    }

    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Product> getProductsByRating(Double minRating) {
        return productRepository.findHighlyRatedProducts(minRating);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findDiscountedProducts();
    }

    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateProductStock(Long id, int quantity) {
        productRepository.findById(id).ifPresent(product -> {
            product.setStock(quantity);
            productRepository.save(product);
        });
    }

    // Category methods
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

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Manufacturer methods
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    @Transactional
    public Manufacturer saveManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    // Review methods
    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }

    @Transactional
    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct().getId());
        return savedReview;
    }

    @Transactional
    public void updateProductRating(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            List<Review> reviews = reviewRepository.findByProduct(product);
            if (!reviews.isEmpty()) {
                double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
                product.setRating(averageRating);
                productRepository.save(product);
            }
        });
    }

    // Configurator methods
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
} 