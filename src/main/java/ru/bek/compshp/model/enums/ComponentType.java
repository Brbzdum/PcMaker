package ru.bek.compshp.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Перечисление типов компонентов компьютера
 */
public enum ComponentType {
    // Компоненты ПК
    CPU("Процессор"),
    GPU("Видеокарта"),
    MB("Материнская плата"),
    RAM("Оперативная память"),
    PSU("Блок питания"),
    CASE("Корпус"),
    COOLER("Система охлаждения"),
    STORAGE("Накопитель"),
    
    // Периферийные устройства
    MONITOR("Монитор"),
    KEYBOARD("Клавиатура"),
    MOUSE("Мышь"),
    HEADSET("Гарнитура"),
    SPEAKERS("Колонки"),
    WEBCAM("Web-камера"),
    PRINTER("Принтер"),
    SCANNER("Сканер"),
    GAMEPAD("Игровой контроллер"),
    NETWORK("Сетевое оборудование"),
    HEADPHONES("Наушники"),
    MOUSEPAD("Коврик для мыши"),
    MICROPHONE("Микрофон");

    private final String displayName;
    
    // Список обязательных компонентов для конфигурации ПК
    private static final List<ComponentType> REQUIRED_COMPONENTS = Arrays.asList(
        CPU, MB, RAM, PSU, STORAGE, CASE
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
} 