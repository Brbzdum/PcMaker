package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "pc_configurations")
public class PCConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConfigComponent> components;

    // TODO: Добавить метод для расчета общей стоимости конфигурации
    // TODO: Добавить метод для проверки совместимости всех компонентов
    // TODO: Добавить метод для добавления/удаления компонентов
    // TODO: Добавить метод для клонирования конфигурации
    // TODO: Добавить метод для экспорта конфигурации в PDF
    // TODO: Добавить метод для проверки наличия всех необходимых компонентов
    // TODO: Добавить метод для расчета энергопотребления
} 