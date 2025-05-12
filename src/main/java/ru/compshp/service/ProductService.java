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
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CompatibilityRuleRepository ruleRepository;

    // Product methods
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    public List<Product> getProductsByManufacturer(String manufacturer) {
        return productRepository.findByManufacturerName(manufacturer);
    }

    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> getProductsByRating(Double minRating) {
        return productRepository.findByRatingGreaterThanEqual(minRating);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> getPopularProducts() {
        return productRepository.findTop10ByOrderByRatingDesc();
    }

    public List<Product> getNewArrivals() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
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

    // Category methods
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Manufacturer methods
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Optional<Manufacturer> getManufacturerByName(String name) {
        return manufacturerRepository.findByName(name);
    }

    @Transactional
    public Manufacturer saveManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        manufacturerRepository.deleteById(id);
    }

    // Review methods
    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Transactional
    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct().getId());
        return savedReview;
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.findById(id).ifPresent(review -> {
            Long productId = review.getProduct().getId();
            reviewRepository.deleteById(id);
            updateProductRating(productId);
        });
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findAll().stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsWithDiscount() {
        return productRepository.findAll().stream()
                .filter(p -> p.getDiscount() != null && p.getDiscount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByRating(double minRating) {
        return productRepository.findAll().stream()
                .filter(p -> p.getRating() != null && p.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductAndApprovedTrue(product);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            product.setRating(averageRating);
            productRepository.save(product);
        }
    }

    public List<Product> getCompatibleProducts(Product sourceProduct) {
        return productRepository.findAll().stream()
                .filter(p -> !p.equals(sourceProduct))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategoryAndPriceRange(Category category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByCategory(category).stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsWithDiscountByCategory(Category category) {
        return productRepository.findByCategory(category).stream()
                .filter(p -> p.getDiscount() != null && p.getDiscount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    public void updateProductStock(Product product, int quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    public List<Product> getProductsByComponentType(ComponentType componentType) {
        return productRepository.findByComponentType(componentType);
    }

    public boolean isInStock(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= quantity)
                .orElse(false);
    }

    public BigDecimal calculateDiscountedPrice(Product product) {
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            return product.getPrice().multiply(BigDecimal.ONE.subtract(product.getDiscount().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
        }
        return product.getPrice();
    }
} 