package ru.bek.compshp.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Перечисление типов компонентов компьютера
 */
public enum ComponentType {
    CPU("Процессор"),
    GPU("Видеокарта"),
    MB("Материнская плата"),
    RAM("Оперативная память"),
    PSU("Блок питания"),
    CASE("Корпус"),
    COOLER("Система охлаждения"),
    STORAGE("Накопитель"),
    MONITOR("Монитор"),
    KEYBOARD("Клавиатура"),
    MOUSE("Мышь"),
    HEADPHONES("Наушники"),
    SPEAKERS("Колонки");

    private final String displayName;
    
    // Список обязательных компонентов для конфигурации ПК
    private static final List<ComponentType> REQUIRED_COMPONENTS = Arrays.asList(
        CPU, MB, RAM, PSU, STORAGE, CASE
    );
    
    // Список компонентов, которые могут присутствовать в нескольких экземплярах
    private static final List<ComponentType> MULTIPLE_ALLOWED_COMPONENTS = Arrays.asList(
        RAM, STORAGE, GPU, COOLER, MONITOR, HEADPHONES, SPEAKERS
    );
    
    // Список периферийных устройств
    private static final List<ComponentType> PERIPHERAL_COMPONENTS = Arrays.asList(
        MONITOR, KEYBOARD, MOUSE, HEADPHONES, SPEAKERS
    );

    ComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Проверяет, является ли компонент обязательным для базовой конфигурации ПК
     * @return true если компонент обязательный, иначе false
     */
    public boolean isRequired() {
        return REQUIRED_COMPONENTS.contains(this);
    }
    
    /**
     * Проверяет, может ли компонент присутствовать в нескольких экземплярах в конфигурации
     * @return true если компонент может быть в нескольких экземплярах, иначе false
     */
    public boolean allowsMultiple() {
        return MULTIPLE_ALLOWED_COMPONENTS.contains(this);
    }
    
    /**
     * Проверяет, является ли компонент периферийным устройством
     * @return true если компонент является периферией, иначе false
     */
    public boolean isPeripheral() {
        return PERIPHERAL_COMPONENTS.contains(this);
    }
} 