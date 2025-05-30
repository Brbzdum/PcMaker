package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.*;
import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.compshp.exception.ResourceNotFoundException;

/**
 * Сервис для управления продуктами
 * Основные функции:
 * - Управление продуктами (CRUD)
 * - Фильтрация и поиск продуктов
 * - Управление складом
 * - Проверка совместимости компонентов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ManufacturerService manufacturerService;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Получает продукт по его ID
     * @param id ID продукта
     * @return продукт
     * @throws ResourceNotFoundException если продукт не найден
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerService.getAllManufacturers();
    }

    public List<Product> getProductsByManufacturerId(Long manufacturerId) {
        log.info("Fetching products for manufacturerId: {}", manufacturerId);
        List<Product> products = productRepository.findByManufacturerId(manufacturerId);
        log.info("Found products: {}", products);
        return products;
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    @Transactional
    public void saveProductWithImage(Product product, MultipartFile mainImageFile) throws IOException {
        try {
            // Сохраняем продукт, чтобы он получил ID
            Product savedProduct = productRepository.save(product);

            if (mainImageFile != null && !mainImageFile.isEmpty()) {
                // productId точно не null, т.к. уже сохранён
                String imagePath = saveImageToFileSystem(mainImageFile, savedProduct.getId());
                savedProduct.setImagePath(imagePath);

                // Сохраняем ещё раз, чтоб обновить поле imagePath
                productRepository.save(savedProduct);
            }

            log.info("Транзакция выполнена успешно (saveProductWithImage).");
        } catch (Exception e) {
            log.error("Ошибка: транзакция откатывается.", e);
            throw e;
        }
    }

    private String saveImageToFileSystem(MultipartFile file, Long productId) throws IOException {
        File directory = new File(uploadDir); // что-то вроде "uploads/images/products"
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        // Имя файла: product_17_timestamp.jpg
        String fileName = "product_" + productId + "_" + System.currentTimeMillis() + "." + extension;
        File destination = new File(directory, fileName);

        file.transferTo(destination);

        // Вернём относительный путь
        return "/uploads/images/products/" + fileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg"; // Default extension
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    @Transactional
    public void updateProductWithImage(Product product, MultipartFile mainImageFile) throws IOException {
        try {
            if (mainImageFile != null && !mainImageFile.isEmpty()) {
                String imagePath = saveImageToFileSystem(mainImageFile, product.getId());
                product.setImagePath(imagePath);
            }
            productRepository.save(product);
            log.info("Транзакция обновления выполнена успешно.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении продукта: ", e);
            throw e;
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }

    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        int updatedStock = product.getStock() - quantity;
        if (updatedStock < 0) {
            throw new IllegalStateException("Недостаточно товара на складе");
        }
        product.setStock(updatedStock);
        productRepository.save(product);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Product> getProductsByMinRating(Double minRating) {
        return productRepository.findByMinRating(minRating);
    }

    public List<Product> getProductsByMinDiscount(Integer minDiscount) {
        return productRepository.findByMinDiscount(minDiscount);
    }

    public List<Product> getReadyPCs(Long categoryId) {
        return productRepository.findReadyPCs(categoryId);
    }

    public List<Product> findBySpecsContaining(String spec) {
        return productRepository.findBySpecsContaining(spec);
    }

    public List<Product> getProductsByType(ComponentType type) {
        return productRepository.findByComponentType(type);
    }

    public List<Product> getProductsBySpecs(Map<String, String> specs) {
        return productRepository.findBySpecs(specs);
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        
        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setComponentType(product.getComponentType());
        existingProduct.setSpecs(product.getSpecs());
        existingProduct.setStock(product.getStock());
        existingProduct.setIsActive(product.getIsActive());
        
        return productRepository.save(existingProduct);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Double getProductRating(Long productId) {
        return reviewRepository.calculateAverageRating(productId);
    }

    // Configurator specific methods
    public List<Product> getComponentsInBudget(ComponentType type, BigDecimal maxPrice) {
        return productRepository.findComponentsInBudget(type, maxPrice);
    }

    public List<Product> getComponentsByMinPerformance(ComponentType type, Double minPerformance) {
        return productRepository.findComponentsByMinPerformance(type, minPerformance);
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
    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerService.getManufacturerById(id);
    }

    public Manufacturer saveManufacturer(Manufacturer manufacturer) {
        return manufacturerService.saveManufacturer(manufacturer);
    }

    /**
     * Получает средний рейтинг продукта на основе отзывов
     * @param productId ID продукта
     * @return средний рейтинг или 0.0, если отзывов нет
     */
    public BigDecimal getAverageRating(Long productId) {
        Product product = getProductById(productId);
        if (product.getReviews() == null || product.getReviews().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double avgRating = product.getReviews().stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
            
        return BigDecimal.valueOf(avgRating);
    }
    
    /**
     * Получает количество товара на складе
     * @param productId ID продукта
     * @return количество товара
     */
    public Integer getStockQuantity(Long productId) {
        return getProductById(productId).getStock();
    }
    
    /**
     * Получает спецификацию продукта по ключу
     * @param productId ID продукта
     * @param key ключ спецификации
     * @return значение спецификации или пустая строка, если спецификация не найдена
     */
    public String getSpec(Long productId, String key) {
        Product product = getProductById(productId);
        Map<String, String> specs = product.getSpecs();
        
        if (specs == null) {
            return "";
        }
        return specs.getOrDefault(key, "");
    }
    
    /**
     * Получает все спецификации продукта
     * @param productId ID продукта
     * @return карта спецификаций
     */
    public Map<String, String> getSpecifications(Long productId) {
        return getProductById(productId).getSpecs();
    }
    
    /**
     * Получает название продукта
     * @param productId ID продукта
     * @return название продукта
     */
    public String getProductName(Long productId) {
        return getProductById(productId).getTitle();
    }

    /**
     * Получает все продукты из категорий, которые являются компонентами ПК
     * @return список продуктов из категорий компонентов ПК
     */
    public List<Product> getPcComponentProducts() {
        List<Category> pcComponentCategories = categoryRepository.findByPcComponent(true);
        return pcComponentCategories.stream()
                .flatMap(category -> productRepository.findByCategoryId(category.getId()).stream())
                .toList();
    }
} 