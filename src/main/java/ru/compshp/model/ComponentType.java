package ru.compshp.model;

public enum ComponentType {
    CPU("Процессор"),
    GPU("Видеокарта"),
    MB("Материнская плата"),
    RAM("Оперативная память"),
    PSU("Блок питания"),
    CASE("Корпус"),
    COOLER("Охлаждение"),
    STORAGE("Накопитель"),
    SSD("SSD накопитель"),
    HDD("Жесткий диск");

    private final String displayName;

    ComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // TODO: Добавить метод для получения всех типов компонентов с их отображаемыми именами
    // TODO: Добавить метод для проверки совместимости типов компонентов
    // TODO: Добавить метод для получения обязательных спецификаций для каждого типа
    // TODO: Добавить метод для валидации спецификаций компонента
} 