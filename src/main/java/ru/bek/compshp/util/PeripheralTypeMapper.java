package ru.bek.compshp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.repository.CategoryRepository;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Утилитный класс для маппинга между ID категорий периферии и типами периферии,
 * используемыми в конфигураторе.
 * Решает проблему несоответствия между бэкендом и фронтендом в именовании типов периферии.
 */
@Component
public class PeripheralTypeMapper {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // Карта соответствия ID категорий типам периферии
    private Map<Long, String> CATEGORY_ID_TO_TYPE;
    
    // Карта соответствия типов периферии ID категорий
    private Map<String, Long> TYPE_TO_CATEGORY_ID;
    
    // Карта соответствия slug'ов категорий типам периферии
    private Map<String, String> SLUG_TO_TYPE;
    
    @PostConstruct
    public void init() {
        // Загружаем все периферийные категории
        List<Category> peripheralCategories = categoryRepository.findByIsPeripheral(true);
        
        Map<Long, String> idToType = new HashMap<>();
        Map<String, Long> typeToId = new HashMap<>();
        Map<String, String> slugToType = new HashMap<>();
        
        // Заполняем карты соответствий на основе данных из базы
        for (Category category : peripheralCategories) {
            String type = mapSlugToType(category.getSlug());
            idToType.put(category.getId(), type);
            typeToId.put(type, category.getId());
            if (category.getSlug() != null) {
                slugToType.put(category.getSlug().toLowerCase(), type);
            }
        }
        
        CATEGORY_ID_TO_TYPE = Collections.unmodifiableMap(idToType);
        TYPE_TO_CATEGORY_ID = Collections.unmodifiableMap(typeToId);
        SLUG_TO_TYPE = Collections.unmodifiableMap(slugToType);
    }
    
    /**
     * Маппит slug категории в тип периферии
     */
    private String mapSlugToType(String slug) {
        if (slug == null) return null;
        
        String lowerSlug = slug.toLowerCase();
        switch (lowerSlug) {
            case "monitors": case "monitor": return "monitor";
            case "keyboards": case "keyboard": return "keyboard";
            case "mice": case "mouse": return "mouse";
            case "headsets": case "headset": case "headphones": return "headset";
            case "speakers": case "speaker": return "speakers";
            case "mousepads": case "mousepad": return "mousepad";
            case "microphones": case "microphone": return "microphone";
            default: return lowerSlug;
        }
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
     * Получает тип периферии по slug категории
     * @param slug slug категории
     * @return тип периферии или null, если slug не соответствует периферии
     */
    public String getPeripheralTypeBySlug(String slug) {
        if (slug == null) return null;
        return SLUG_TO_TYPE.get(slug.toLowerCase());
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