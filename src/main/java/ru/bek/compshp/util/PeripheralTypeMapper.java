package ru.bek.compshp.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Утилитный класс для маппинга между ID категорий периферии и типами периферии,
 * используемыми в конфигураторе.
 * Решает проблему несоответствия между бэкендом и фронтендом в именовании типов периферии.
 */
@Component
public class PeripheralTypeMapper {
    
    // Карта соответствия ID категорий типам периферии
    private static final Map<Long, String> CATEGORY_ID_TO_TYPE;
    
    // Карта соответствия типов периферии ID категорий
    private static final Map<String, Long> TYPE_TO_CATEGORY_ID;
    
    static {
        Map<Long, String> idToType = new HashMap<>();
        // Заполняем карту соответствий
        idToType.put(38L, "monitor");   // Мониторы
        idToType.put(39L, "keyboard");  // Клавиатуры
        idToType.put(40L, "mouse");     // Мыши
        idToType.put(41L, "headset");   // Гарнитуры
        idToType.put(42L, "speakers");  // Колонки
        idToType.put(43L, "mousepad");  // Коврики
        idToType.put(44L, "microphone"); // Микрофоны
        
        CATEGORY_ID_TO_TYPE = Collections.unmodifiableMap(idToType);
        
        // Создаем обратную карту
        Map<String, Long> typeToId = new HashMap<>();
        for (Map.Entry<Long, String> entry : idToType.entrySet()) {
            typeToId.put(entry.getValue(), entry.getKey());
        }
        TYPE_TO_CATEGORY_ID = Collections.unmodifiableMap(typeToId);
    }
    
    /**
     * Получает тип периферии по ID категории
     * @param categoryId ID категории
     * @return тип периферии или null, если категория не является периферией
     */
    public String getPeripheralTypeByCategoryId(Long categoryId) {
        return CATEGORY_ID_TO_TYPE.get(categoryId);
    }
    
    /**
     * Получает ID категории по типу периферии
     * @param peripheralType тип периферии
     * @return ID категории или null, если тип не соответствует ни одной категории
     */
    public Long getCategoryIdByPeripheralType(String peripheralType) {
        if (peripheralType == null) {
            return null;
        }
        // Приводим к нижнему регистру для единообразия
        String normalizedType = peripheralType.toLowerCase();
        return TYPE_TO_CATEGORY_ID.get(normalizedType);
    }
    
    /**
     * Проверяет, является ли категория периферией
     * @param categoryId ID категории
     * @return true, если категория является периферией
     */
    public boolean isPeripheralCategory(Long categoryId) {
        return CATEGORY_ID_TO_TYPE.containsKey(categoryId);
    }
    
    /**
     * Проверяет, является ли тип периферией
     * @param type тип для проверки
     * @return true, если тип является периферией
     */
    public boolean isPeripheralType(String type) {
        if (type == null) {
            return false;
        }
        return TYPE_TO_CATEGORY_ID.containsKey(type.toLowerCase());
    }
} 