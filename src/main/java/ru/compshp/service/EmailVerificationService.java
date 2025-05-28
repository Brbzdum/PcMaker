package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для верификации email пользователей
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${app.verification-token-expiry:24}")
    private int tokenExpiryHours;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Создает токен верификации и отправляет письмо для подтверждения email
     * @param user пользователь, которому отправляется письмо
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        // Генерация уникального токена
        String token = UUID.randomUUID().toString();
        
        // Сохранение токена и времени его истечения
        user.setVerificationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(tokenExpiryHours));
        userRepository.save(user);

        // Формирование и отправка письма
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Подтверждение регистрации на сайте PC Maker");
        mailMessage.setText("Для подтверждения email перейдите по ссылке: " 
                + baseUrl + "/auth/verify-email?token=" + token);
        
        mailSender.send(mailMessage);
    }

    /**
     * Проверяет токен верификации и активирует аккаунт пользователя
     * @param token токен верификации
     * @return true, если верификация прошла успешно
     */
    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElse(null);
        
        if (user == null || user.isEmailVerified() || 
                user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Активация аккаунта
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Повторно отправляет письмо для подтверждения email
     * @param email email пользователя
     * @return true, если письмо отправлено успешно
     */
    @Transactional
    public boolean resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);
                
        if (user == null || user.isEmailVerified()) {
            return false;
        }
        
        sendVerificationEmail(user);
        return true;
    }
} 