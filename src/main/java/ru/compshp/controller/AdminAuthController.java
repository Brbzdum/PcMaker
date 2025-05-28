package ru.compshp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для обработки авторизации в админ-панель
 */
@Controller
@RequestMapping("/api/auth")
public class AdminAuthController {

    /**
     * Страница входа в админ-панель
     * @return имя представления
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }
} 