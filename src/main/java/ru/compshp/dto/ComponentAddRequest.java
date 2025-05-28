package ru.compshp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для добавления компонента в конфигурацию
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentAddRequest {
    @NotNull(message = "ID конфигурации не может быть пустым")
    private Long configurationId;
    
    @NotNull(message = "ID продукта не может быть пустым")
    private Long productId;
    
    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;
} 