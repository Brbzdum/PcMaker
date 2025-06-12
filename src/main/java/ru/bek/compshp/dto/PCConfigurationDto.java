package ru.bek.compshp.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для передачи данных конфигурации ПК
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCConfigurationDto {
    private Long id;
    
    private Long userId;
    
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String name;
    
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;
    
    private BigDecimal totalPrice;
    
    private Boolean isPublic;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<ConfigComponentDto> components = new ArrayList<>();
    
    private Boolean isCompatible;
    
    private List<String> compatibilityIssues;
    
    private Double totalPerformance;
} 