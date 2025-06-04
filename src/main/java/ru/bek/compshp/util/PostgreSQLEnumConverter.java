package ru.bek.compshp.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.bek.compshp.model.enums.ComponentType;

/**
 * Конвертер для преобразования между ComponentType и строковым представлением для PostgreSQL Enum
 */
@Converter(autoApply = true)
public class PostgreSQLEnumConverter implements AttributeConverter<ComponentType, String> {

    @Override
    public String convertToDatabaseColumn(ComponentType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public ComponentType convertToEntityAttribute(String dbData) {
        return dbData != null ? ComponentType.valueOf(dbData) : null;
    }
} 