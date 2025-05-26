package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.compshp.exception.BusinessException;
import ru.compshp.exception.CartNotFoundException;
import ru.compshp.exception.InsufficientStockException;
import ru.compshp.exception.ProductNotFoundException;
import ru.compshp.model.Cart;
import ru.compshp.model.CartItem;
import ru.compshp.service.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam @NotNull Long userId) {
        try {
            return ResponseEntity.ok(cartService.getOrCreateCart(userId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addToCart(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(productId);
        } catch (InsufficientStockException e) {
            throw new InsufficientStockException(productId, e.getAvailable(), e.getRequested());
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId) {
        try {
            return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateQuantity(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.updateQuantity(userId, productId, quantity));
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(productId);
        } catch (InsufficientStockException e) {
            throw new InsufficientStockException(productId, e.getAvailable(), e.getRequested());
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam @NotNull Long userId) {
        try {
            return ResponseEntity.ok(cartService.getCartItems(userId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calculateTotal(@RequestParam @NotNull Long userId) {
        try {
            return ResponseEntity.ok(cartService.calculateTotal(userId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam @NotNull Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @GetMapping("/empty")
    public ResponseEntity<Boolean> isCartEmpty(@RequestParam @NotNull Long userId) {
        try {
            return ResponseEntity.ok(cartService.isCartEmpty(userId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemsCount(@RequestParam @NotNull Long userId) {
        try {
            return ResponseEntity.ok(cartService.getCartItemsCount(userId));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }
} 