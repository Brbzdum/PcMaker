package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String website;

    @OneToMany(mappedBy = "manufacturer")
    private Set<Product> products;

    // TODO: Добавить метод для получения всех продуктов производителя
    // TODO: Добавить метод для получения статистики продаж
    // TODO: Добавить метод для получения рейтинга производителя
    // TODO: Добавить метод для получения популярных продуктов
    // TODO: Добавить метод для валидации данных производителя
    // TODO: Добавить метод для получения категорий продуктов
    // TODO: Добавить метод для получения общей прибыли
} 