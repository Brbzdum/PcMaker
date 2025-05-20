package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public Cart getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        CartItemId cartItemId = new CartItemId();
        cartItemId.setCartId(cart.getId());
        cartItemId.setProductId(productId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setId(cartItemId);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setCartId(cart.getId());
            cartItemId.setProductId(productId);
            cartItemRepository.deleteById(cartItemId);
        }
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setCartId(cart.getId());
            cartItemId.setProductId(productId);

            cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
                Product product = cartItem.getProduct();
                if (product.getStock() < quantity) {
                    throw new RuntimeException("Not enough stock");
                }
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            });
        }
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    public BigDecimal calculateTotal(Long userId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            return BigDecimal.ZERO;
        }

        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart != null ? List.copyOf(cart.getItems()) : List.of();
    }
} 