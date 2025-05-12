package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Cart;
import ru.compshp.model.CartItem;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.repository.CartRepository;
import ru.compshp.repository.UserRepository;
import ru.compshp.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public ResponseEntity<?> getCart() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> createNewCart(user));
        
        return ResponseEntity.ok(cart);
    }

    @Transactional
    public ResponseEntity<?> addToCart(Long productId, Integer quantity) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productService.getProduct(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> createNewCart(user));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        cart = cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @Transactional
    public ResponseEntity<?> addConfigurationToCart(PCConfiguration configuration) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> createNewCart(user));

        // Добавляем CPU
        addToCart(configuration.getCpu().getId(), 1);

        // Добавляем материнскую плату
        addToCart(configuration.getMotherboard().getId(), 1);

        // Добавляем GPU
        addToCart(configuration.getGpu().getId(), 1);

        // Добавляем PSU
        addToCart(configuration.getPsu().getId(), 1);

        // Добавляем корпус
        addToCart(configuration.getCase().getId(), 1);

        // Добавляем RAM
        for (Product ram : configuration.getRam()) {
            addToCart(ram.getId(), 1);
        }

        // Добавляем накопители
        for (Product storage : configuration.getStorage()) {
            addToCart(storage.getId(), 1);
        }

        cart = cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @Transactional
    public ResponseEntity<?> updateCartItem(Long itemId, Integer quantity) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        cart = cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @Transactional
    public ResponseEntity<?> removeFromCart(Long itemId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getId().equals(itemId));

        cart = cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @Transactional
    public ResponseEntity<?> clearCart() {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cart = cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
} 