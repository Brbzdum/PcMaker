package ru.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.compshp.model.enums.ComponentType;

import java.math.BigDecimal;

/**
 * DTO для компонента конфигурации компьютера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComponentDto {
    private Long id;
    private Long productId;
    private String productName;
    private ComponentType type;
    private BigDecimal price;
} 