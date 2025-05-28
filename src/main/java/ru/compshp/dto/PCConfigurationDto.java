package ru.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для конфигурации компьютера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCConfigurationDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private BigDecimal totalPrice;
    private Double performanceScore;
    private Boolean isCompatible;
    private List<ConfigComponentDto> components;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 