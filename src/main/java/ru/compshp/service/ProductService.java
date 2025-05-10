package ru.compshp.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CompatibilityRuleRepository ruleRepository;

    public List<Product> getAvailableProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByManufacturer(Manufacturer manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
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

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
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