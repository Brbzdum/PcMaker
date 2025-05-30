package ru.compshp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для обработки авторизации в админ-панель
 */
@Controller
public class AdminAuthController {

    /**
     * Страница входа в админ-панель
     * @param error параметр ошибки аутентификации
     * @param logout параметр выхода из системы
     * @param model модель для передачи данных в представление
     * @return имя представления
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Неправильное имя пользователя или пароль");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        
        return "admin/login";
    }
} 