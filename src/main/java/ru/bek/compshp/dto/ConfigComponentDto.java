package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.model.enums.ComponentType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    private String peripheralType;
    private BigDecimal price;
    private String manufacturerName;
    private Long manufacturerId;
    
    @Builder.Default
    private Map<String, String> specs = new HashMap<>();
} 