package ru.bek.compshp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для создания конфигурации компьютера с компонентами
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationWithComponentsRequest {
    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;
    
    @NotBlank(message = "Название конфигурации не может быть пустым")
    private String name;
    
    private String description;
    
    private String category;
    
    @NotNull(message = "Список компонентов не может быть пустым")
    private List<Long> componentIds;
} 