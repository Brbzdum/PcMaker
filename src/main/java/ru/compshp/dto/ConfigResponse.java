package ru.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private BigDecimal totalPrice;
    private Double totalPerformance;
    private Boolean isCompatible;
    private List<Map<String, Object>> components;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 