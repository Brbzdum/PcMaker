package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.dto.LoginRequest;
import ru.compshp.dto.SignupRequest;
import ru.compshp.exception.ResourceNotFoundException;
import ru.compshp.model.Role;
import ru.compshp.model.User;
import ru.compshp.model.enums.RoleName;
import ru.compshp.repository.RoleRepository;
import ru.compshp.repository.UserRepository;
import ru.compshp.security.JwtUtils;
import ru.compshp.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для аутентификации и регистрации пользователей
 */
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    /**
     * Регистрирует нового пользователя
     * @param signupRequest данные для регистрации
     * @return созданный пользователь
     * @throws IllegalStateException если пользователь с таким email или username уже существует
     */
    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        // Проверяем, что пользователь с таким email или username не существует
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalStateException("Пользователь с таким email уже существует");
        }
        
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalStateException("Пользователь с таким username уже существует");
        }
        
        // Создаем нового пользователя
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setName(signupRequest.getName());
        
        // Устанавливаем роли
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));
        roles.add(userRole);
        
        boolean isAdmin = false;
        
        // Если в запросе указаны роли, добавляем их
        if (signupRequest.getRoles() != null && !signupRequest.getRoles().isEmpty()) {
            for (String roleName : signupRequest.getRoles()) {
                try {
                    RoleName enumRoleName = RoleName.valueOf(roleName);
                    Role role = roleRepository.findByName(enumRoleName)
                            .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName));
                    roles.add(role);
                    
                    if (enumRoleName == RoleName.ROLE_ADMIN) {
                        isAdmin = true;
                    }
                } catch (IllegalArgumentException e) {
                    // Игнорируем неизвестные роли
                }
            }
        }
        
        user.setRoles(roles);
        
        // Если это администратор, сразу активируем аккаунт
        // Если обычный пользователь, то активация после подтверждения email
        if (isAdmin) {
            user.setActive(true);
            user.setActivationCode(null);
        } else {
            user.setActive(false); // Не активен до подтверждения email
            user.setActivationCode(UUID.randomUUID().toString());
            
            // Отправляем письмо для подтверждения email через EmailService
            try {
                String siteURL = "http://localhost:8080"; // Базовый URL, может быть изменен через конфигурацию
                emailService.sendVerificationEmail(user, siteURL);
            } catch (Exception e) {
                // Логирование ошибки, но не прерываем регистрацию
                // Пользователь может запросить повторную отправку письма
            }
        }
        
        user.setCreatedAt(LocalDateTime.now());
        
        // Сохраняем пользователя
        User savedUser = userRepository.save(user);
        
        return savedUser;
    }

    /**
     * Аутентифицирует пользователя и генерирует JWT-токен
     * @param loginRequest данные для входа
     * @return ResponseEntity с JWT-токеном и информацией о пользователе
     */
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("id", userDetails.getId());
        response.put("username", userDetails.getUsername());
        response.put("email", userDetails.getEmail());
        response.put("roles", userDetails.getAuthorities());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Верифицирует пользователя по коду
     * @param code код верификации
     * @return ResponseEntity с сообщением о результате
     */
    @Transactional
    public ResponseEntity<?> verifyUser(String code) {
        boolean verified = emailService.verifyEmail(code);
        
        if (!verified) {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
        
        return ResponseEntity.ok("User verified successfully");
    }

    /**
     * Отправляет запрос на сброс пароля
     * @param email email пользователя
     * @return ResponseEntity с сообщением о результате
     */
    @Transactional
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String token = UUID.randomUUID().toString();
        // Сохраняем токен сброса пароля и время его истечения
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        
        // Отправка email через EmailService
        try {
            emailService.sendPasswordReset(user, token);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить письмо для сброса пароля", e);
        }
        
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    /**
     * Сбрасывает пароль пользователя
     * @param token токен сброса пароля
     * @param password новый пароль
     * @return ResponseEntity с сообщением о результате
     */
    @Transactional
    public ResponseEntity<?> resetPassword(String token, String password) {
        User user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен сброса пароля истек");
        }
        
        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        return ResponseEntity.ok("Password has been reset successfully");
    }

    /**
     * Получение текущего аутентифицированного пользователя
     * @return текущий пользователь или null
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Проверяет, является ли пользователь администратором
     * @param user пользователь для проверки
     * @return true, если пользователь имеет роль ADMIN
     */
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        
        return new CustomUserDetails(user);
    }
} 