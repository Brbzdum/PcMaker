package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.UserDto;
import ru.compshp.dto.UserRegistrationDto;
import ru.compshp.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO: Реализовать получение профиля текущего пользователя
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile() {
        // TODO: Получить профиль пользователя по токену/сессии
        return ResponseEntity.ok(/* userDto */ null);
    }

    // TODO: Реализовать регистрацию пользователя
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto registrationDto) {
        // TODO: Зарегистрировать пользователя и вернуть его DTO
        return ResponseEntity.ok(/* userDto */ null);
    }

    // TODO: Реализовать обновление профиля пользователя
    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
        // TODO: Обновить профиль пользователя
        return ResponseEntity.ok(/* userDto */ null);
    }

    // TODO: Реализовать получение заказов пользователя
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders() {
        // TODO: Вернуть список заказов пользователя
        return ResponseEntity.ok(/* orders */ null);
    }

    // TODO: Реализовать получение конфигураций пользователя
    @GetMapping("/configurations")
    public ResponseEntity<?> getUserConfigurations() {
        // TODO: Вернуть список конфигураций пользователя
        return ResponseEntity.ok(/* configurations */ null);
    }

    // TODO: Реализовать получение отзывов пользователя
    @GetMapping("/reviews")
    public ResponseEntity<?> getUserReviews() {
        // TODO: Вернуть список отзывов пользователя
        return ResponseEntity.ok(/* reviews */ null);
    }

    // TODO: Реализовать смену пароля, восстановление пароля, активацию аккаунта и т.д.
} 