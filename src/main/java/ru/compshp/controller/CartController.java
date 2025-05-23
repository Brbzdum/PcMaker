package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.Cart;
import ru.compshp.model.CartItem;
import ru.compshp.service.CartService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(
            @RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull Long productId,
            @RequestParam @Min(1) int quantity) {
        return ResponseEntity.ok(cartService.addItem(userId, productId, quantity));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItem(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId,
            @RequestParam @Min(0) int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping
    public ResponseEntity<Cart> clearCart(
            @RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(
            @RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(
            @RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(cartService.getCartTotal(userId));
    }
} 