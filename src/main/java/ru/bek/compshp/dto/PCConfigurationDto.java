package ru.bek.compshp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    
    @NotBlank(message = "Название конфигурации не может быть пустым")
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String name;
    
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;
    
    @NotNull(message = "Процессор обязателен")
    private Long cpuId;
    
    @NotNull(message = "Материнская плата обязательна")
    private Long motherboardId;
    
    @NotNull(message = "Видеокарта обязательна")
    private Long gpuId;
    
    @NotNull(message = "Блок питания обязателен")
    private Long psuId;
    
    @NotNull(message = "Корпус обязателен")
    private Long caseId;
    
    @NotNull(message = "Оперативная память обязательна")
    private Set<Long> ramIds;
    
    @NotNull(message = "Накопитель обязателен")
    private Set<Long> storageIds;
    
    private BigDecimal totalPrice;
    
    private Boolean isPublic;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<ConfigComponentDto> components;
    
    private Boolean isCompatible;
    
    private List<String> compatibilityIssues;
    
    private Double totalPerformance;
} 