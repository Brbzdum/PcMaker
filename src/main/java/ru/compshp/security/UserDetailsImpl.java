package ru.compshp.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.compshp.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация UserDetails для работы с пользователями в Spring Security
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    /**
     * Конструктор для создания UserDetailsImpl из объекта User
     */
    public UserDetailsImpl(Long id, String username, String email, String password,
                          Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    /**
     * Создает UserDetailsImpl из объекта User
     * @param user объект пользователя
     * @return объект UserDetailsImpl
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.isActive()
        );
    }

    /**
     * Получает права пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Получает ID пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Получает email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Получает пароль пользователя
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Получает имя пользователя
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Проверяет, не истек ли срок действия аккаунта
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, не заблокирован ли аккаунт
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, не истек ли срок действия учетных данных
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активирован ли аккаунт
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 