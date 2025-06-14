package ru.bek.compshp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для регистрации пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов")
    private String username;
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 40, message = "Пароль должен содержать от 6 до 40 символов")
    private String password;
    
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    
    @AssertTrue(message = "Необходимо согласие на обработку персональных данных")
    private boolean dataProcessingConsent;
} 