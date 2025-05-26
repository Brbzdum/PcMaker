package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
    }

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

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Проверяем наличие на складе
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Not enough stock");
        }
        
        // Проверяем, есть ли уже такой товар в корзине
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElse(null);
        
        if (cartItem != null) {
            // Если товар уже есть, увеличиваем количество
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
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

    @Transactional
    public Cart updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        
        CartItemId cartItemId = new CartItemId(cart.getId(), productId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        
        Product product = cartItem.getProduct();
        
        // Проверяем наличие на складе
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Not enough stock");
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        cart.getItems().clear();
        cart = cartRepository.save(cart);
        
        return cart;
    }

    public BigDecimal calculateTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getTotalPrice();
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream().toList();
    }

    public boolean isCartEmpty(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.isEmpty();
    }

    public int getCartItemsCount(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItemsCount();
    }
} 