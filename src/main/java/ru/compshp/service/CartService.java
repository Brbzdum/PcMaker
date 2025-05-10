package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
    }

    public Optional<Cart> getByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Transactional
    public Cart addItem(User user, Long productId, int quantity) {
        Cart cart = getByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getTitle());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItem(User user, Long productId, int quantity) {
        Cart cart = getByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            if (item.getProduct().getStockQuantity() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + item.getProduct().getTitle());
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(User user, Long productId) {
        Cart cart = getByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(User user) {
        Cart cart = getByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().forEach(cartItemRepository::delete);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    public BigDecimal calculateTotalPrice(User user) {
        return getByUser(user)
                .map(cart -> cart.getItems().stream()
                        .map(item -> {
                            BigDecimal price = item.getProduct().getPrice();
                            BigDecimal discount = item.getProduct().getDiscount() != null ? item.getProduct().getDiscount() : BigDecimal.ZERO;
                            BigDecimal discountedPrice = price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
                            return discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    public boolean checkAllItemsInStock(User user) {
        return getByUser(user)
                .map(cart -> cart.getItems().stream()
                        .allMatch(item -> item.getProduct().getStockQuantity() >= item.getQuantity()))
                .orElse(true);
    }

    @Transactional
    public Order checkout(User user) {
        Cart cart = getByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        if (!checkAllItemsInStock(user)) {
            throw new RuntimeException("Some items are out of stock");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(calculateTotalPrice(user));
        order = orderRepository.save(order);

        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal price = cartItem.getProduct().getPrice();
            BigDecimal discount = cartItem.getProduct().getDiscount() != null ? cartItem.getProduct().getDiscount() : BigDecimal.ZERO;
            BigDecimal discountedPrice = price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
            orderItem.setPrice(discountedPrice);
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
            // Update product stock
            cartItem.getProduct().setStockQuantity(cartItem.getProduct().getStockQuantity() - cartItem.getQuantity());
            productRepository.save(cartItem.getProduct());
        });

        clearCart(user);
        return orderRepository.save(order);
    }

    public List<Product> getRecommendedProducts(User user) {
        // TODO: Реализовать рекомендацию товаров
        return List.of();
    }

    // TODO: Добавить метод для создания корзины
    // TODO: Добавить метод для добавления товара в корзину
    // TODO: Добавить метод для обновления количества товара
    // TODO: Добавить метод для удаления товара из корзины
    // TODO: Добавить метод для очистки корзины
    // TODO: Добавить метод для расчета стоимости корзины
    // TODO: Добавить метод для проверки наличия товаров
    // TODO: Добавить метод для применения скидок
    // TODO: Добавить метод для сохранения корзины
    // TODO: Добавить метод для восстановления корзины
    // TODO: Добавить метод для получения рекомендуемых товаров
    // TODO: Добавить метод для проверки совместимости компонентов
    // TODO: Добавить метод для создания заказа из корзины
} 