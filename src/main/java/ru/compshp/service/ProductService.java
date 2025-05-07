package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ProductCategory;
import ru.compshp.model.Manufacturer;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.ProductCompatibilityRepository;
import ru.compshp.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCompatibilityRepository compatibilityRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ProductCompatibilityRepository compatibilityRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.compatibilityRepository = compatibilityRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getByManufacturer(Manufacturer manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
    }

    public List<Product> searchByTitle(String title) {
        return productRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public boolean isInStock(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(p -> p.getStock() >= quantity)
                .orElse(false);
    }

    @Transactional
    public void updateRating(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            double averageRating = reviewRepository.findByProduct(product).stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            product.setRating(averageRating);
            productRepository.save(product);
        });
    }

    public List<Product> getPopularProducts() {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Product> getCompatibleProducts(Long productId) {
        return productRepository.findById(productId)
                .map(product -> compatibilityRepository.findBySourceProduct(product)
                        .stream()
                        .map(pc -> pc.getTargetProduct())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    public List<Product> getRecommendedProducts(Long userId) {
        // TODO: Implement recommendation system based on user's purchase history and preferences
        return getPopularProducts();
    }

    public double calculateDiscount(Product product) {
        if (product.getDiscount() != null && product.getDiscount() > 0) {
            return product.getPrice() * (product.getDiscount() / 100.0);
        }
        return 0.0;
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setTitle(updatedProduct.getTitle());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStock(updatedProduct.getStock());
                    existingProduct.setCategory(updatedProduct.getCategory());
                    existingProduct.setManufacturer(updatedProduct.getManufacturer());
                    existingProduct.setDiscount(updatedProduct.getDiscount());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByManufacturer(Manufacturer manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByTitleContainingIgnoreCase(query);
    }

    public List<Product> filterProducts(ProductCategory category, Manufacturer manufacturer, Double minPrice, Double maxPrice) {
        return productRepository.findAll().stream()
                .filter(p -> category == null || p.getCategory() == category)
                .filter(p -> manufacturer == null || p.getManufacturer().equals(manufacturer))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getDiscount() != null && p.getDiscount() > 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStock(Long productId, int quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        });
    }

    public List<Product> getSimilarProducts(Long productId) {
        return productRepository.findById(productId)
                .map(product -> productRepository.findByCategory(product.getCategory()).stream()
                        .filter(p -> !p.getId().equals(productId))
                        .limit(5)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
} 