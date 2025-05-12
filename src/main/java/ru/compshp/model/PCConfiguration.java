package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

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

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "cpu_id")
    private Product cpu;

    @ManyToOne
    @JoinColumn(name = "motherboard_id")
    private Product motherboard;

    @ManyToOne
    @JoinColumn(name = "gpu_id")
    private Product gpu;

    @ManyToOne
    @JoinColumn(name = "psu_id")
    private Product psu;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Product pcCase;

    @ManyToMany
    @JoinTable(
        name = "configuration_ram",
        joinColumns = @JoinColumn(name = "configuration_id"),
        inverseJoinColumns = @JoinColumn(name = "ram_id")
    )
    private Set<Product> ram = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "configuration_storage",
        joinColumns = @JoinColumn(name = "configuration_id"),
        inverseJoinColumns = @JoinColumn(name = "storage_id")
    )
    private Set<Product> storage = new HashSet<>();

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "power_requirement")
    private Integer powerRequirement;

    @Column(name = "is_compatible")
    private boolean compatible;

    @Column(name = "compatibility_notes", columnDefinition = "jsonb")
    private String compatibilityNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // TODO: Добавить метод для расчета общей стоимости конфигурации
    // TODO: Добавить метод для проверки совместимости всех компонентов
    // TODO: Добавить метод для добавления/удаления компонентов
    // TODO: Добавить метод для клонирования конфигурации
    // TODO: Добавить метод для экспорта конфигурации в PDF
    // TODO: Добавить метод для проверки наличия всех необходимых компонентов
    // TODO: Добавить метод для расчета энергопотребления
} 