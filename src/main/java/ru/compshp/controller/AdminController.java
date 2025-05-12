package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ProductDTO;
import ru.compshp.dto.UserDTO;
import ru.compshp.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Управление товарами
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return adminService.createProduct(productDTO);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return adminService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return adminService.deleteProduct(id);
    }

    // Управление пользователями
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return adminService.updateUser(id, userDTO);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return adminService.deleteUser(id);
    }

    // Управление заказами
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return adminService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        return adminService.getOrder(id);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return adminService.updateOrderStatus(id, status);
    }

    // Статистика
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return adminService.getStatistics();
    }
} 