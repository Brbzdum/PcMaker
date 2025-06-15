package ru.bek.compshp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO для регистрации в системе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Имя пользователя может содержать только буквы, цифры и знак подчеркивания")
    private String username;
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 120, message = "Пароль должен быть от 6 до 120 символов")
    private String password;
    
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    
    @AssertTrue(message = "Необходимо согласие на обработку персональных данных")
    private boolean dataProcessingConsent;
    
    private Set<String> roles;
} 