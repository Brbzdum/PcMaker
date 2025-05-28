package ru.compshp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.compshp.exception.CategoryNotFoundException;

import java.util.function.Supplier;

/**
 * DTO для передачи данных категории
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    private String name;

    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    private String description;

    private Long parentId;
    
    private Integer productCount;
    
    private Boolean hasChildren;
    
    /**
     * Метод для совместимости с Optional.orElseThrow
     * @param exceptionSupplier поставщик исключения
     * @param <X> тип исключения
     * @return этот объект
     * @throws X если объект равен null
     */
    public <X extends Throwable> CategoryDto orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.id == null) {
            throw exceptionSupplier.get();
        }
        return this;
    }
} 