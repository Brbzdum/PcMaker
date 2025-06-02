package ru.bek.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.UserProfileDto;
import ru.bek.compshp.model.User;
import ru.bek.compshp.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String activationCode) {
        userService.activateUser(activationCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long id,
            @PathVariable String roleName) {
        userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long id,
            @PathVariable String roleName) {
        userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok().build();
    }

    // Профиль пользователя
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileDto profileDto) {
        return userService.updateProfile(profileDto);
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