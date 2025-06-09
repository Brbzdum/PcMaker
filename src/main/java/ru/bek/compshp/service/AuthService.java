package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.dto.LoginRequest;
import ru.bek.compshp.dto.SignupRequest;
import ru.bek.compshp.exception.ResourceNotFoundException;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.repository.RoleRepository;
import ru.bek.compshp.repository.UserRepository;
import ru.bek.compshp.security.JwtUtils;
import ru.bek.compshp.security.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для аутентификации и управления пользователями
 */
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    
    private ApplicationContext applicationContext;
    private AuthenticationManager authenticationManager;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Autowired
    public AuthService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtUtils jwtUtils, 
                       EmailService emailService,
                       ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.applicationContext = applicationContext;
    }
    
    // Lazy load AuthenticationManager to break circular dependency
    private AuthenticationManager getAuthenticationManager() {
        if (this.authenticationManager == null) {
            this.authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        }
        return this.authenticationManager;
    }

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
                log.info("Отправка письма верификации для пользователя: {}", user.getEmail());
                emailService.sendVerificationEmail(user, baseUrl);
                log.info("Письмо верификации успешно отправлено для: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Ошибка при отправке письма верификации: {}", e.getMessage(), e);
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
        try {
        Authentication authentication = getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Проверяем, активирован ли аккаунт
            if (!userDetails.isEnabled()) {
                return ResponseEntity.status(401).body("Необходимо подтвердить email перед входом");
            }
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("id", userDetails.getId());
        response.put("username", userDetails.getUsername());
        response.put("email", userDetails.getEmail());
        response.put("roles", userDetails.getAuthorities());
        
        return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Неверное имя пользователя или пароль");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка аутентификации: " + e.getMessage());
        }
    }

    /**
     * Верифицирует пользователя по коду и генерирует токен для автоматического входа
     * @param code код верификации
     * @return Map с токеном и информацией о пользователе или сообщение об ошибке
     */
    @Transactional
    public Map<String, Object> verifyUserAndLogin(String code) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userRepository.findByActivationCode(code)
                .orElse(null);
        
        if (user == null) {
            result.put("error", "Недействительный код подтверждения");
            return result;
        }
        
        // Активация аккаунта
        user.setActive(true);
        user.setActivationCode(null);
        userRepository.save(user);
        
        // Отправляем приветственное письмо
        try {
            emailService.sendWelcomeEmail(user);
        } catch (Exception e) {
            log.error("Ошибка при отправке приветственного письма: {}", e.getMessage());
        }
        
        // Генерируем токен для автоматического входа
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                null,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                        .collect(Collectors.toList())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Формируем ответ с токеном и информацией о пользователе
        result.put("token", jwt);
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        
        return result;
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
            return ResponseEntity.badRequest().body("Недействительный код подтверждения");
        }
        
        return ResponseEntity.ok("Пользователь успешно подтвержден");
    }

    /**
     * Отправляет запрос на сброс пароля
     * @param email email пользователя
     * @return ResponseEntity с сообщением о результате
     */
    @Transactional
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Пользователь с email " + email + " не найден"));
        
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
        
        return ResponseEntity.ok("Ссылка для сброса пароля отправлена на ваш email");
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
            .orElseThrow(() -> new ResourceNotFoundException("Недействительный токен"));
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен сброса пароля истек");
        }
        
        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        return ResponseEntity.ok("Пароль успешно сброшен");
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
} 