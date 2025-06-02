package ru.bek.compshp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.LoginRequest;
import ru.bek.compshp.dto.SignupRequest;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.service.AuthService;
import ru.bek.compshp.service.EmailService;
import ru.bek.compshp.service.RoleService;

/**
 * Контроллер для аутентификации и регистрации пользователей
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final RoleService roleService;

    /**
     * Регистрация нового пользователя
     * @param signupRequest данные нового пользователя
     * @param request HTTP запрос для получения URL сайта
     * @return сообщение о результате регистрации
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest, 
                                        HttpServletRequest request) {
        // Создаем нового пользователя
        User user = authService.registerUser(signupRequest);
        
        // Добавляем роль USER
        Role userRole = roleService.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.addRole(userRole);
        
        // Письмо с подтверждением отправляется в сервисе AuthService
        
        return ResponseEntity.ok("User registered successfully! Please check your email for verification.");
    }

    /**
     * Аутентификация пользователя
     * @param loginRequest данные для входа
     * @return JWT токен и информация о пользователе
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    /**
     * Верификация email пользователя
     * @param code код верификации из email
     * @return сообщение о результате верификации
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
        return authService.verifyUser(code);
    }

    /**
     * Запрос на сброс пароля
     * @param email email пользователя
     * @return сообщение о результате запроса
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        return authService.forgotPassword(email);
    }

    /**
     * Сброс пароля
     * @param token токен для сброса пароля
     * @param password новый пароль
     * @return сообщение о результате сброса пароля
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                         @RequestParam("password") String password) {
        return authService.resetPassword(token, password);
    }
} 