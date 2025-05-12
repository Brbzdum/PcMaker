package ru.compshp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.LoginRequest;
import ru.compshp.dto.SignupRequest;
import ru.compshp.model.Role;
import ru.compshp.model.User;
import ru.compshp.service.AuthService;
import ru.compshp.service.EmailService;
import ru.compshp.service.RoleService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final RoleService roleService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest, 
                                        HttpServletRequest request) {
        // Создаем нового пользователя
        User user = authService.registerUser(signupRequest);
        
        // Добавляем роль USER
        Role userRole = roleService.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.addRole(userRole);
        
        // Отправляем письмо с подтверждением
        String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        try {
            emailService.sendVerificationEmail(user, siteURL);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Could not send verification email");
        }

        return ResponseEntity.ok("User registered successfully! Please check your email for verification.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
        return authService.verifyUser(code);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                         @RequestParam("password") String password) {
        return authService.resetPassword(token, password);
    }
} 