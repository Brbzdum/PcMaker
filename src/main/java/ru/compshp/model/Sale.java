package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "total_profit", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalProfit;

    // TODO: Добавить метод для расчета прибыли
    // TODO: Добавить метод для получения статистики продаж
    // TODO: Добавить метод для получения прибыли за период
    // TODO: Добавить метод для получения популярных товаров
    // TODO: Добавить метод для получения статистики по категориям
    // TODO: Добавить метод для экспорта данных о продажах
    // TODO: Добавить метод для анализа трендов продаж
} 