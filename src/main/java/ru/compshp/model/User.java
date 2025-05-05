package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    // TODO: Добавить метод для проверки роли пользователя
    // TODO: Добавить метод для активации аккаунта
    // TODO: Добавить метод для смены пароля
    // TODO: Добавить метод для получения заказов пользователя
    // TODO: Добавить метод для получения корзины пользователя
    // TODO: Добавить метод для получения конфигураций ПК пользователя
    // TODO: Добавить метод для проверки прав доступа
} 