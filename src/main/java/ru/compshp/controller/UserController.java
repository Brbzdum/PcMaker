package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.UserProfileDTO;
import ru.compshp.service.UserService;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Профиль пользователя
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileDTO profileDTO) {
        return userService.updateProfile(profileDTO);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        return userService.changePassword(oldPassword, newPassword);
    }

    // Заказы пользователя
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders() {
        return userService.getUserOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id) {
        return userService.getOrderDetails(id);
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        return userService.cancelOrder(id);
    }

    // Корзина пользователя
    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        return userService.getCart();
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                     @RequestParam Integer quantity) {
        return userService.addToCart(productId, quantity);
    }

    @PutMapping("/cart/update")
    public ResponseEntity<?> updateCartItem(@RequestParam Long cartItemId,
                                          @RequestParam Integer quantity) {
        return userService.updateCartItem(cartItemId, quantity);
    }

    @DeleteMapping("/cart/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long cartItemId) {
        return userService.removeFromCart(cartItemId);
    }

    // Конфигурации ПК
    @GetMapping("/configs")
    public ResponseEntity<?> getUserConfigs() {
        return userService.getUserConfigs();
    }

    @PostMapping("/configs")
    public ResponseEntity<?> createConfig(@RequestBody String configJson) {
        return userService.createConfig(configJson);
    }

    @PutMapping("/configs/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id,
                                        @RequestBody String configJson) {
        return userService.updateConfig(id, configJson);
    }

    @DeleteMapping("/configs/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        return userService.deleteConfig(id);
    }
} 