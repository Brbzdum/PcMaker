package ru.compshp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Product;
import ru.compshp.model.Review;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.ReviewRepository;
import ru.compshp.service.ProductService;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductServiceImpl(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByManufacturer(Long manufacturerId) {
        return productRepository.findByManufacturerId(manufacturerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByComponentType(ComponentType componentType) {
        return productRepository.findByComponentType(componentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getHighlyRatedProducts(Double minRating) {
        return productRepository.findHighlyRatedProducts(minRating);
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        });
    }

    @Override
    public void updateRating(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            List<Review> reviews = reviewRepository.findByProductId(productId);
            if (!reviews.isEmpty()) {
                double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
                product.setAverageRating(averageRating);
                product.setRatingsCount(reviews.size());
                productRepository.save(product);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long productId, Integer quantity) {
        return productRepository.findById(productId)
            .map(product -> product.getStock() >= quantity)
            .orElse(false);
    }
} 