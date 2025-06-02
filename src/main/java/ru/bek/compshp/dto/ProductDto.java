package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO для передачи данных продукта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private Long id;
    
    @NotBlank(message = "Название продукта не может быть пустым")
    private String title;
    
    private String description;
    
    @NotNull(message = "Цена продукта должна быть указана")
    @Min(value = 0, message = "Цена продукта не может быть отрицательной")
    private BigDecimal price;
    
    @NotNull(message = "Количество продукта на складе должно быть указано")
    @Min(value = 0, message = "Количество продукта не может быть отрицательным")
    private Integer stock;
    
    private String componentType;
    
    private Long manufacturerId;
    
    private Long categoryId;
    
    private Map<String, String> specs;
    
    private boolean isActive;
} 