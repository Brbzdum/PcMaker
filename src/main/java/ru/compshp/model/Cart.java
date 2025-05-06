package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items;

    // TODO: Добавить метод для расчета общей стоимости корзины
    // TODO: Добавить метод для добавления товара в корзину
    // TODO: Добавить метод для удаления товара из корзины
    // TODO: Добавить метод для обновления количества товара
    // TODO: Добавить метод для очистки корзины
    // TODO: Добавить метод для проверки наличия товаров
    // TODO: Добавить метод для создания заказа из корзины
} 