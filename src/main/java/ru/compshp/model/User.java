package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import ru.compshp.model.enums.UserRole;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    private boolean enabled = false;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // TODO: Добавить метод для проверки роли пользователя
    // TODO: Добавить метод для активации аккаунта
    // TODO: Добавить метод для смены пароля
    // TODO: Добавить метод для получения заказов пользователя
    // TODO: Добавить метод для получения корзины пользователя
    // TODO: Добавить метод для получения конфигураций ПК пользователя
    // TODO: Добавить метод для проверки прав доступа
} 