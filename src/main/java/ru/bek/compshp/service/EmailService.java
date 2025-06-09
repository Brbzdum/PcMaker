package ru.bek.compshp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendOrderConfirmation(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("email/order-confirmation", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Подтверждение заказа #" + order.getId(),
            emailContent
        );
    }

    public void sendOrderStatusUpdate(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("email/order-status-update", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Обновление статуса заказа #" + order.getId(),
            emailContent
        );
    }

    public void sendPasswordReset(User user, String resetToken) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", baseUrl + "/api/auth/reset-password?token=" + resetToken);
        
        String emailContent = templateEngine.process("email/password-reset", context);
        sendHtmlEmail(
            user.getEmail(),
            "Сброс пароля",
            emailContent
        );
    }

    public void sendWelcomeEmail(User user) {
        Context context = new Context();
        context.setVariable("user", user);
        
        String emailContent = templateEngine.process("email/welcome", context);
        sendHtmlEmail(
            user.getEmail(),
            "Добро пожаловать в PcMaker!",
            emailContent
        );
    }

    public void sendCustomEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        
        String emailContent = templateEngine.process("email/" + templateName, context);
        sendHtmlEmail(to, subject, emailContent);
    }

    public void sendRegistrationConfirmation(User user, String activationToken) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("activationToken", activationToken);
        context.setVariable("URL", baseUrl + "/api/auth/verify?code=" + activationToken);
        
        String emailContent = templateEngine.process("email/registration-confirmation", context);
        sendHtmlEmail(
            user.getEmail(),
            "Подтверждение регистрации в PcMaker",
            emailContent
        );
    }

    public void sendOrderCancellation(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("email/order-cancellation", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Отмена заказа #" + order.getId(),
            emailContent
        );
    }

    public void sendOrderDelivery(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("email/order-delivery", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Ваш заказ #" + order.getId() + " отправлен",
            emailContent
        );
    }

    public void sendAccountActivation(User user, String activationToken) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("activationToken", activationToken);
        
        String emailContent = templateEngine.process("account-activation", context);
        sendHtmlEmail(
            user.getEmail(),
            "Активация аккаунта PcMaker",
            emailContent
        );
    }

    public void sendProductAvailabilityNotification(User user, Product product) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("product", product);
        
        String emailContent = templateEngine.process("product-availability", context);
        sendHtmlEmail(
            user.getEmail(),
            "Товар " + product.getTitle() + " снова в наличии!",
            emailContent
        );
    }

    public void sendDiscountNotification(User user, Map<String, Object> discountInfo) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("discountInfo", discountInfo);
        
        String emailContent = templateEngine.process("discount-notification", context);
        sendHtmlEmail(
            user.getEmail(),
            "Специальные предложения в PcMaker",
            emailContent
        );
    }

    public void sendNewProductsNotification(User user, List<Product> newProducts) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("newProducts", newProducts);
        
        String emailContent = templateEngine.process("new-products", context);
        sendHtmlEmail(
            user.getEmail(),
            "Новые поступления в PcMaker",
            emailContent
        );
    }

    public void sendOrderReturnNotification(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("order-return", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Возврат заказа #" + order.getId(),
            emailContent
        );
    }

    public void sendOrderProblemNotification(Order order, String problemDescription) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        context.setVariable("problemDescription", problemDescription);
        
        String emailContent = templateEngine.process("order-problem", context);
        sendHtmlEmail(
            order.getUser().getEmail(),
            "Уведомление о проблеме с заказом #" + order.getId(),
            emailContent
        );
    }

    public void sendVerificationEmail(User user, String siteURL) throws MessagingException {
        log.info("Начинаем отправку письма верификации для пользователя: {}", user.getEmail());
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("URL", siteURL + "/api/auth/verify?code=" + user.getActivationCode());
        context.setVariable("activationToken", user.getActivationCode());

        String emailContent = templateEngine.process("email/registration-confirmation", context);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(mailUsername);
        helper.setTo(user.getEmail());
        helper.setSubject("Подтверждение регистрации в PC Maker");
        helper.setText(emailContent, true);

        log.info("Подготовлено письмо для отправки от {} к {}", mailUsername, user.getEmail());
        
        try {
        mailSender.send(message);
            log.info("Письмо успешно отправлено на {}", user.getEmail());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Повторно отправляет письмо для подтверждения email
     * @param email email пользователя
     * @return true, если письмо отправлено успешно
     */
    @Transactional
    public boolean resendVerificationEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElse(null);
                
        if (user == null || user.getActive()) {
            return false;
        }
        
        // Генерируем новый код активации, если его нет
        if (user.getActivationCode() == null) {
            user.setActivationCode(UUID.randomUUID().toString());
            userRepository.save(user);
        }
        
        // Отправляем письмо
        sendVerificationEmail(user, baseUrl);
        return true;
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
        
        // Отправляем приветственное письмо
        sendWelcomeEmail(user);
        
        return true;
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("Начинаем отправку HTML письма для: {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(mailUsername);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            log.info("Подготовлено HTML письмо для отправки от {} к {}", mailUsername, to);
            
            mailSender.send(message);
            log.info("HTML письмо успешно отправлено на {}", to);
        } catch (MessagingException e) {
            log.error("Ошибка при отправке HTML письма: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 