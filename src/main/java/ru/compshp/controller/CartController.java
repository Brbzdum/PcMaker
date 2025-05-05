package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.CartDto;
import ru.compshp.dto.CartItemDto;
import ru.compshp.service.CartService;
import ru.compshp.service.ProductService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    // TODO: Получить корзину пользователя
    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        // TODO: Вернуть корзину текущего пользователя
        return ResponseEntity.ok(/* cartDto */ null);
    }

    // TODO: Добавить товар в корзину
    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@RequestBody CartItemDto cartItemDto) {
        // TODO: Добавить товар в корзину
        return ResponseEntity.ok(/* cartDto */ null);
    }

    // TODO: Обновить количество товара в корзине
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDto> updateItem(@PathVariable Long productId, @RequestBody CartItemDto cartItemDto) {
        // TODO: Обновить количество товара
        return ResponseEntity.ok(/* cartDto */ null);
    }

    // TODO: Удалить товар из корзины
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable Long productId) {
        // TODO: Удалить товар из корзины
        return ResponseEntity.ok(/* cartDto */ null);
    }

    // TODO: Очистить корзину
    @DeleteMapping
    public ResponseEntity<CartDto> clearCart() {
        // TODO: Очистить корзину
        return ResponseEntity.ok(/* cartDto */ null);
    }

    // TODO: Оформить заказ из корзины
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        // TODO: Создать заказ из корзины
        return ResponseEntity.ok(/* orderDto */ null);
    }

    // TODO: Методы для расчета стоимости, проверки наличия, применения скидок и т.д.
} 