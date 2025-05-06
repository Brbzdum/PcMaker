package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import ru.compshp.model.enums.UserRole;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(nullable = false, length = 1000)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles;

    // TODO: Добавить метод для проверки роли пользователя
    // TODO: Добавить метод для активации аккаунта
    // TODO: Добавить метод для смены пароля
    // TODO: Добавить метод для получения заказов пользователя
    // TODO: Добавить метод для получения корзины пользователя
    // TODO: Добавить метод для получения конфигураций ПК пользователя
    // TODO: Добавить метод для проверки прав доступа
} 