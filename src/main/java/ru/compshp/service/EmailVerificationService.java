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

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Отправляет письмо для подтверждения email
     * @param user пользователь, которому отправляется письмо
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        // Проверяем, есть ли у пользователя код активации
        if (user.getActivationCode() == null) {
            user.setActivationCode(UUID.randomUUID().toString());
            userRepository.save(user);
        }

        // Формирование и отправка письма
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Подтверждение регистрации на сайте PC Maker");
        mailMessage.setText("Для подтверждения email перейдите по ссылке: " 
                + baseUrl + "/auth/verify-email?code=" + user.getActivationCode());
        
        mailSender.send(mailMessage);
    }

    /**
     * Отправляет письмо для сброса пароля
     * @param user пользователь, которому отправляется письмо
     */
    @Transactional
    public void sendPasswordResetEmail(User user) {
        // Формирование и отправка письма
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Сброс пароля на сайте PC Maker");
        mailMessage.setText("Для сброса пароля перейдите по ссылке: " 
                + baseUrl + "/auth/reset-password?token=" + user.getResetToken());
        
        mailSender.send(mailMessage);
    }

    /**
     * Проверяет код активации и активирует аккаунт пользователя
     * @param code код активации
     * @return true, если верификация прошла успешно
     */
    @Transactional
    public boolean verifyEmail(String code) {
        User user = userRepository.findByActivationCode(code)
                .orElse(null);
        
        if (user == null) {
            return false;
        }
        
        // Активация аккаунта
        user.setActive(true);
        user.setActivationCode(null);
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
                
        if (user == null || user.getActive()) {
            return false;
        }
        
        // Генерируем новый код активации
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);
        
        sendVerificationEmail(user);
        return true;
    }
} 