package ru.bek.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.CartDto;
import ru.bek.compshp.dto.CartItemDto;
import ru.bek.compshp.exception.BusinessException;
import ru.bek.compshp.exception.CartNotFoundException;
import ru.bek.compshp.exception.InsufficientStockException;
import ru.bek.compshp.exception.ProductNotFoundException;
import ru.bek.compshp.model.Cart;
import ru.bek.compshp.model.CartItem;
import ru.bek.compshp.service.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(@RequestParam @NotNull Long userId) {
        try {
            Cart cart = cartService.getOrCreateCart(userId);
            return ResponseEntity.ok(CartDto.fromEntity(cart));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addToCart(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        try {
            Cart cart = cartService.addToCart(userId, productId, quantity);
            return ResponseEntity.ok(CartDto.fromEntity(cart));
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(productId);
        } catch (InsufficientStockException e) {
            throw new InsufficientStockException(productId, e.getAvailable(), e.getRequested());
        } catch (Exception e) {
            // Логируем неожиданные ошибки
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDto> removeFromCart(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId) {
        try {
            Cart cart = cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok(CartDto.fromEntity(cart));
        } catch (BusinessException e) {
            throw new CartNotFoundException(userId);
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDto> updateQuantity(
            @RequestParam @NotNull Long userId,
            @PathVariable @NotNull Long productId,
            @RequestParam @Min(1) Integer quantity) {
        try {
            Cart cart = cartService.updateQuantity(userId, productId, quantity);
            return ResponseEntity.ok(CartDto.fromEntity(cart));
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(productId);
        } catch (InsufficientStockException e) {
            throw new InsufficientStockException(productId, e.getAvailable(), e.getRequested());
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getCartItems(@RequestParam @NotNull Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            List<CartItemDto> cartItemDtos = cartItems.stream()
                    .map(CartItemDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cartItemDtos);
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