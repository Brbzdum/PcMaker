package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.model.*;
import ru.bek.compshp.repository.CartItemRepository;
import ru.bek.compshp.repository.CartRepository;
import ru.bek.compshp.repository.ProductRepository;
import ru.bek.compshp.repository.UserRepository;
import ru.bek.compshp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для работы с корзиной покупок
 */
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    /**
     * Получает корзину пользователя по его ID
     * @param userId ID пользователя
     * @return корзина пользователя
     * @throws ResourceNotFoundException если корзина не найдена
     */
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
    }

    /**
     * Получает существующую корзину пользователя или создает новую
     * @param userId ID пользователя
     * @return существующая или новая корзина
     */
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            });
    }

    /**
     * Добавляет продукт в корзину пользователя
     * @param userId ID пользователя
     * @param productId ID продукта
     * @param quantity количество продукта
     * @return обновленная корзина
     * @throws IllegalStateException если недостаточно товара на складе
     */
    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Проверяем наличие на складе
        if (productService.getStockQuantity(productId) < quantity) {
            throw new IllegalStateException("Not enough stock");
        }
        
        // Проверяем, есть ли уже такой товар в корзине
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElse(null);
        
        if (cartItem != null) {
            // Если товар уже есть, увеличиваем количество
            int newQuantity = cartItem.getQuantity() + quantity;
            if (productService.getStockQuantity(productId) < newQuantity) {
                throw new IllegalStateException("Not enough stock");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            // Если товара нет, создаем новую позицию
            cartItem = new CartItem();
            cartItem.setId(cartItemId);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }
        
        cartItemRepository.save(cartItem);
        return cartRepository.save(cart);
    }

    /**
     * Удаляет продукт из корзины пользователя
     * @param userId ID пользователя
     * @param productId ID продукта
     * @return обновленная корзина
     */
    @Transactional
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        });
        
        return cartRepository.save(cart);
    }

    /**
     * Обновляет количество продукта в корзине
     * @param userId ID пользователя
     * @param productId ID продукта
     * @param quantity новое количество
     * @return обновленная корзина
     * @throws IllegalStateException если недостаточно товара на складе
     */
    @Transactional
    public Cart updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        
        Product product = cartItem.getProduct();
        
        // Проверяем наличие на складе
        if (productService.getStockQuantity(product.getId()) < quantity) {
            throw new IllegalStateException("Not enough stock");
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        return cartRepository.save(cart);
    }

    /**
     * Очищает корзину пользователя
     * @param userId ID пользователя
     * @return пустая корзина
     */
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        cart.getItems().clear();
        cart = cartRepository.save(cart);
        
        return cart;
    }

    /**
     * Вычисляет общую стоимость корзины
     * @param userId ID пользователя
     * @return общая стоимость корзины
     */
    public BigDecimal calculateTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получает список всех товаров в корзине
     * @param userId ID пользователя
     * @return список товаров в корзине
     */
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream().toList();
    }

    /**
     * Проверяет, пуста ли корзина
     * @param userId ID пользователя
     * @return true если корзина пуста, иначе false
     */
    public boolean isCartEmpty(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().isEmpty();
    }

    /**
     * Получает общее количество товаров в корзине
     * @param userId ID пользователя
     * @return количество товаров
     */
    public int getCartItemsCount(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Проверяет, есть ли товар в достаточном количестве на складе
     * @param cartItem товар в корзине
     * @return true если товар в наличии, иначе false
     */
    public boolean isInStock(CartItem cartItem) {
        return productService.getStockQuantity(cartItem.getProduct().getId()) >= cartItem.getQuantity();
    }

    /**
     * Обновляет количество товара с проверкой наличия
     * @param cartItem товар в корзине
     * @param newQuantity новое количество
     * @throws IllegalArgumentException если количество меньше или равно 0, или больше наличия на складе
     */
    public void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (newQuantity > productService.getStockQuantity(cartItem.getProduct().getId())) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }

    /**
     * Проверяет, достигнуто ли максимальное количество товара
     * @param cartItem товар в корзине
     * @return true если достигнуто максимальное количество, иначе false
     */
    public boolean isMaxQuantityReached(CartItem cartItem) {
        return cartItem.getQuantity() >= productService.getStockQuantity(cartItem.getProduct().getId());
    }

    /**
     * Рассчитывает общую стоимость позиции в корзине
     * @param cartItem товар в корзине
     * @return общая стоимость позиции
     */
    public BigDecimal calculateCartItemTotal(CartItem cartItem) {
        if (cartItem.getProduct() == null || cartItem.getProduct().getPrice() == null || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    /**
     * Получает список корзин с количеством товаров больше указанного
     * @param itemCount минимальное количество товаров
     * @return список корзин
     */
    public List<Cart> getCartsWithManyItems(Integer itemCount) {
        return cartRepository.findByItemsCountGreaterThan(itemCount);
    }
} 