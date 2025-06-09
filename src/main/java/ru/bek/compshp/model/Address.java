package ru.bek.compshp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для работы с адресами
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String fullAddress;
    private String city;
    private String street;
    private String building;
    private String apartment;
    private String zipCode;
    
    @Override
    public String toString() {
        return fullAddress != null ? fullAddress : "";
    }
} 