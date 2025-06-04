package ru.bek.compshp.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Конвертер для преобразования строки с характеристиками (ключ: значение) в Map
 */
@Component
@Slf4j
public class StringToMapConverter implements Converter<String, Map<String, String>> {

    @Override
    public Map<String, String> convert(String source) {
        Map<String, String> result = new HashMap<>();
        
        log.info("Конвертирую строку в Map: '{}'", source);
        
        // Если строка пустая, возвращаем пустую Map
        if (source == null || source.trim().isEmpty()) {
            log.info("Строка пустая, возвращаю пустую Map");
            return result;
        }
        
        // Разбиваем строку на строки по переносу строки
        String[] lines = source.split("\\r?\\n");
        log.info("Разбито на {} строк", lines.length);
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // Ищем первое двоеточие в строке
            int colonIndex = line.indexOf(':');
            
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = colonIndex < line.length() - 1 ? 
                    line.substring(colonIndex + 1).trim() : "";
                
                if (!key.isEmpty()) {
                    result.put(key, value);
                    log.debug("Добавлена пара ключ-значение: '{}' = '{}'", key, value);
                }
            } else {
                // Если нет двоеточия, используем всю строку как ключ и пустую строку как значение
                result.put(line, "");
                log.debug("Добавлен ключ без значения: '{}'", line);
            }
        }
        
        log.info("Конвертация завершена, создано {} пар ключ-значение", result.size());
        return result;
    }
} 