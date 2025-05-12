package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.compshp.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin
public class CartController {
    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCart() {
        return cartService.getCart();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return cartService.addToCart(productId, quantity);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCartItem(
            @RequestParam Long itemId,
            @RequestParam Integer quantity) {
        return cartService.updateCartItem(itemId, quantity);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromCart(@RequestParam Long itemId) {
        return cartService.removeFromCart(itemId);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> clearCart() {
        return cartService.clearCart();
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkout() {
        return cartService.checkout();
    }
} 