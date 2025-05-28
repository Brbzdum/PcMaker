package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.dto.UserProfileDto;
import ru.compshp.exception.ResourceNotFoundException;
import ru.compshp.model.*;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.model.enums.RoleName;
import ru.compshp.repository.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public User createUser(User user) {
        // Проверяем, что пользователь с таким email или username не существует
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Пользователь с таким email уже существует");
        }
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Пользователь с таким username уже существует");
        }
        
        // Кодируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Генерируем код активации
        user.setActivationCode(UUID.randomUUID().toString());
        
        // По умолчанию новые пользователи неактивны
        user.setActive(false);
        
        // Добавляем роль USER по умолчанию
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_USER));
        user.getRoles().add(userRole);
        
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", user.getId()));
        
        // Обновляем только нужные поля
        existingUser.setName(user.getName());
        
        // Проверяем, меняется ли email
        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Пользователь с таким email уже существует");
            }
            existingUser.setEmail(user.getEmail());
        }
        
        // Проверяем, меняется ли username
        if (!existingUser.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalStateException("Пользователь с таким username уже существует");
            }
            existingUser.setUsername(user.getUsername());
        }
        
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void activateUser(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode)
            .orElseThrow(() -> new ResourceNotFoundException("User", "activationCode", activationCode));
        
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        RoleName roleEnum;
        try {
            roleEnum = RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверное имя роли: " + roleName);
        }
        
        Role role = roleRepository.findByName(roleEnum)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleEnum));
        
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        RoleName roleEnum;
        try {
            roleEnum = RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверное имя роли: " + roleName);
        }
        
        Role roleToRemove = roleRepository.findByName(roleEnum)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleEnum));
        
        user.getRoles().remove(roleToRemove);
        userRepository.save(user);
    }
    
    /**
     * Получить текущего аутентифицированного пользователя
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    /**
     * Получить профиль текущего пользователя
     */
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            User user = getCurrentUser();
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Обновить профиль текущего пользователя
     */
    @Transactional
    public ResponseEntity<?> updateProfile(UserProfileDto profileDto) {
        try {
            User user = getCurrentUser();
            
            // Проверяем, меняется ли email
            if (!user.getEmail().equals(profileDto.getEmail())) {
                if (userRepository.existsByEmail(profileDto.getEmail())) {
                    return ResponseEntity.badRequest().body("Пользователь с таким email уже существует");
                }
                user.setEmail(profileDto.getEmail());
            }
            
            // Проверяем, меняется ли username
            if (!user.getUsername().equals(profileDto.getUsername())) {
                if (userRepository.existsByUsername(profileDto.getUsername())) {
                    return ResponseEntity.badRequest().body("Пользователь с таким username уже существует");
                }
                user.setUsername(profileDto.getUsername());
            }
            
            user.setName(profileDto.getName());
            userRepository.save(user);
            
            return ResponseEntity.ok("Профиль успешно обновлен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Изменить пароль текущего пользователя
     */
    @Transactional
    public ResponseEntity<?> changePassword(String oldPassword, String newPassword) {
        try {
            User user = getCurrentUser();
            
            // Проверяем старый пароль
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body("Неверный текущий пароль");
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Пароль успешно изменен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Получить заказы текущего пользователя
     */
    public ResponseEntity<?> getUserOrders() {
        try {
            User user = getCurrentUser();
            List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Получить детали заказа
     */
    public ResponseEntity<?> getOrderDetails(Long orderId) {
        try {
            User user = getCurrentUser();
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
            
            // Проверяем, принадлежит ли заказ текущему пользователю
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("У вас нет доступа к этому заказу");
            }
            
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Отменить заказ
     */
    @Transactional
    public ResponseEntity<?> cancelOrder(Long orderId) {
        try {
            User user = getCurrentUser();
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
            
            // Проверяем, принадлежит ли заказ текущему пользователю
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("У вас нет доступа к этому заказу");
            }
            
            // Проверяем, можно ли отменить заказ
            if (order.getStatus() == OrderStatus.SHIPPED || 
                order.getStatus() == OrderStatus.DELIVERED || 
                order.getStatus() == OrderStatus.COMPLETED || 
                order.getStatus() == OrderStatus.CANCELLED) {
                return ResponseEntity.badRequest().body("Невозможно отменить заказ в текущем статусе");
            }
            
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            return ResponseEntity.ok("Заказ успешно отменен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Получить корзину пользователя
     */
    public ResponseEntity<?> getCart() {
        try {
            User user = getCurrentUser();
            Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
            
            if (cartOpt.isEmpty()) {
                // Создаем новую корзину, если ее нет
                Cart newCart = new Cart();
                newCart.setUser(user);
                return ResponseEntity.ok(cartRepository.save(newCart));
            }
            
            return ResponseEntity.ok(cartOpt.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Добавить товар в корзину
     */
    @Transactional
    public ResponseEntity<?> addToCart(Long productId, Integer quantity) {
        try {
            User user = getCurrentUser();
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            
            // Проверяем наличие товара
            if (product.getStock() < quantity) {
                return ResponseEntity.badRequest().body("Недостаточно товара на складе");
            }
            
            // Получаем или создаем корзину
            Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
            Cart cart = cartOpt.orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
            
            // Проверяем, есть ли уже товар в корзине
            Optional<CartItem> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
            
            if (cartItemOpt.isPresent()) {
                // Обновляем количество товара
                CartItem cartItem = cartItemOpt.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
            } else {
                // Добавляем новый товар
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
                cart.getItems().add(cartItem);
            }
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Обновить количество товара в корзине
     */
    @Transactional
    public ResponseEntity<?> updateCartItem(Long cartItemId, Integer quantity) {
        try {
            User user = getCurrentUser();
            Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));
            
            // Ищем товар в корзине
            Optional<CartItem> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();
            
            if (cartItemOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Товар не найден в корзине");
            }
            
            CartItem cartItem = cartItemOpt.get();
            
            // Проверяем наличие товара
            if (cartItem.getProduct().getStock() < quantity) {
                return ResponseEntity.badRequest().body("Недостаточно товара на складе");
            }
            
            // Обновляем количество товара
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Удалить товар из корзины
     */
    @Transactional
    public ResponseEntity<?> removeFromCart(Long cartItemId) {
        try {
            User user = getCurrentUser();
            Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));
            
            // Ищем товар в корзине
            Optional<CartItem> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();
            
            if (cartItemOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Товар не найден в корзине");
            }
            
            CartItem cartItem = cartItemOpt.get();
            
            // Удаляем товар из корзины
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Получить конфигурации ПК пользователя
     */
    public ResponseEntity<?> getUserConfigs() {
        try {
            User user = getCurrentUser();
            List<PCConfiguration> configs = pcConfigurationRepository.findByUserId(user.getId());
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Создать новую конфигурацию ПК
     */
    @Transactional
    public ResponseEntity<?> createConfig(String configJson) {
        try {
            User user = getCurrentUser();
            PCConfiguration config = objectMapper.readValue(configJson, PCConfiguration.class);
            config.setUser(user);
            return ResponseEntity.ok(pcConfigurationRepository.save(config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Обновить конфигурацию ПК
     */
    @Transactional
    public ResponseEntity<?> updateConfig(Long configId, String configJson) {
        try {
            User user = getCurrentUser();
            PCConfiguration existingConfig = pcConfigurationRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("PCConfiguration", "id", configId));
            
            // Проверяем, принадлежит ли конфигурация текущему пользователю
            if (!existingConfig.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("У вас нет доступа к этой конфигурации");
            }
            
            PCConfiguration updatedConfig = objectMapper.readValue(configJson, PCConfiguration.class);
            updatedConfig.setId(configId);
            updatedConfig.setUser(user);
            
            return ResponseEntity.ok(pcConfigurationRepository.save(updatedConfig));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Удалить конфигурацию ПК
     */
    @Transactional
    public ResponseEntity<?> deleteConfig(Long configId) {
        try {
            User user = getCurrentUser();
            PCConfiguration config = pcConfigurationRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("PCConfiguration", "id", configId));
            
            // Проверяем, принадлежит ли конфигурация текущему пользователю
            if (!config.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("У вас нет доступа к этой конфигурации");
            }
            
            pcConfigurationRepository.delete(config);
            return ResponseEntity.ok("Конфигурация успешно удалена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 