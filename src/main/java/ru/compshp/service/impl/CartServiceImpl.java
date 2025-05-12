package ru.compshp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.exception.CartNotFoundException;
import ru.compshp.exception.InsufficientStockException;
import ru.compshp.exception.ProductNotFoundException;
import ru.compshp.exception.UserNotFoundException;
import ru.compshp.model.Cart;
import ru.compshp.model.CartItem;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.repository.CartRepository;
import ru.compshp.repository.UserRepository;
import ru.compshp.service.CartService;
import ru.compshp.service.PricingService;
import ru.compshp.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PricingService pricingService;

    @Override
    @Transactional(readOnly = true)
    public Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return cartRepository.findByUser(user)
            .orElseGet(() -> createNewCart(user));
    }

    @Override
    @Transactional
    public Cart addItem(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }

        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> createNewCart(user));

        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(pricingService.calculatePrice(product));
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateItemQuantity(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ProductNotFoundException(productId));

        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart clearCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        cartRepository.delete(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        return cart.getItems();
    }

    @Override
    @Transactional(readOnly = true)
    public double getCartTotal(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException(userId));

        return cart.getItems().stream()
            .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
            .sum();
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
} 