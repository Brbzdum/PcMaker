package ru.compshp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// TODO: Конфигурация безопасности Spring Security
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Включаем CORS и отключаем CSRF для REST API
            .cors().and()
            .csrf().disable()
            // Настройка авторизации эндпоинтов
            .authorizeRequests()
                .antMatchers("/api/home/**", "/api/products/**", "/api/manufacturers/**", "/api/reviews/**").permitAll()
                .anyRequest().authenticated()
            .and()
            // Включаем OAuth2 Login
            .oauth2Login()
                // TODO: Кастомизировать обработку успешной/неуспешной аутентификации
            .and()
            // TODO: Добавить logout, обработку ошибок, кастомные фильтры и т.д.
        ;

        return http.build();
    }

    // TODO: Кастомная настройка CORS, если нужно
} 