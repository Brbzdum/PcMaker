package ru.compshp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.compshp.model.Order;
import ru.compshp.model.User;
import ru.compshp.model.Product;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOrderConfirmation(Order order) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", order.getUser());
        
        String emailContent = templateEngine.process("order-confirmation", context);
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
        
        String emailContent = templateEngine.process("order-status-update", context);
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
        
        String emailContent = templateEngine.process("password-reset", context);
        sendHtmlEmail(
            user.getEmail(),
            "Сброс пароля",
            emailContent
        );
    }

    public void sendWelcomeEmail(User user) {
        Context context = new Context();
        context.setVariable("user", user);
        
        String emailContent = templateEngine.process("welcome", context);
        sendHtmlEmail(
            user.getEmail(),
            "Добро пожаловать в PcMaker!",
            emailContent
        );
    }

    public void sendCustomEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        
        String emailContent = templateEngine.process(templateName, context);
        sendHtmlEmail(to, subject, emailContent);
    }

    public void sendRegistrationConfirmation(User user, String activationToken) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("activationToken", activationToken);
        
        String emailContent = templateEngine.process("registration-confirmation", context);
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
        
        String emailContent = templateEngine.process("order-cancellation", context);
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
        
        String emailContent = templateEngine.process("order-delivery", context);
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
        String toAddress = user.getEmail();
        String subject = "Подтверждение регистрации";
        String content = "Уважаемый [[name]],<br>"
                + "Пожалуйста, перейдите по ссылке ниже для подтверждения регистрации:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ПОДТВЕРДИТЬ</a></h3>"
                + "Спасибо,<br>"
                + "PC Maker.";

        Context context = new Context();
        context.setVariable("name", user.getUsername());
        context.setVariable("URL", siteURL + "/api/auth/verify?code=" + user.getVerificationCode());

        String htmlContent = templateEngine.process("verification-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 