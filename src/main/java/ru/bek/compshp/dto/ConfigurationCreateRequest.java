package ru.bek.compshp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания конфигурации компьютера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationCreateRequest {
    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;
    
    @NotBlank(message = "Название конфигурации не может быть пустым")
    private String name;
    
    private String description;
    
    private Boolean isPublic;
} 