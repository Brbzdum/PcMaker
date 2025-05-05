package ru.compshp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// TODO: Конфигурация безопасности Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // TODO: Настроить безопасность приложения (авторизация, аутентификация, права доступа)
} 