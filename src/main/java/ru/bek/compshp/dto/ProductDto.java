package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
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
    
    private Map<String, String> specs = new HashMap<>();
    
    private boolean isActive;
    
    private MultipartFile imageFile;
    
    private String imagePath;
    
    /**
     * Получить строковое представление спецификаций для формы
     * @return строковое представление спецификаций
     */
    public String getSpecsAsString() {
        if (specs == null || specs.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return sb.toString().trim();
    }
    
    /**
     * Установить спецификации из строкового представления
     * @param specsStr строковое представление спецификаций
     */
    public void setSpecsAsString(String specsStr) {
        // Конвертация произойдет через StringToMapConverter
        // Этот метод здесь только для совместимости с формой
    }
} 