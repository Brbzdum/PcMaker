package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    public ResponseEntity<List<Product>> getProductsByManufacturer(@PathVariable Long manufacturerId) {
        return ResponseEntity.ok(productService.getProductsByManufacturerId(manufacturerId));
    }

    @GetMapping("/component-type/{componentType}")
    public ResponseEntity<List<Product>> getProductsByComponentType(
            @PathVariable ComponentType componentType) {
        return ResponseEntity.ok(productService.getProductsByType(componentType));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer threshold) {
        // Since there's no direct method for low stock products,
        // we'll get all products and filter them on the server side
        List<Product> allProducts = productService.getAllProducts();
        List<Product> lowStockProducts = allProducts.stream()
                .filter(p -> p.getStock() <= threshold)
                .toList();
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/highly-rated")
    public ResponseEntity<List<Product>> getHighlyRatedProducts(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        return ResponseEntity.ok(productService.getProductsByMinRating(minRating));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isProductAvailable(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product.getStock() >= quantity);
    }

    @GetMapping("/pc-components")
    public ResponseEntity<List<Product>> getPcComponentProducts() {
        return ResponseEntity.ok(productService.getPcComponentProducts());
    }
} 