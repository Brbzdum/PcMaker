package ru.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO для передачи данных пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов")
    private String username;
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
    
    @Size(min = 6, max = 120, message = "Пароль должен содержать от 6 до 120 символов")
    private String password;
    
    private String name;
    
    private List<String> roles;
    
    private boolean active;
} 