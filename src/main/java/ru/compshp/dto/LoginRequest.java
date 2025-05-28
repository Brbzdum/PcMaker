package ru.compshp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входа в систему
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
} 