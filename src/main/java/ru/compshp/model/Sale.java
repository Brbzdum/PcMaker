package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sales")
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "total_profit", nullable = false)
    private BigDecimal totalProfit;

    @PrePersist
    protected void onCreate() {
        saleDate = LocalDateTime.now();
    }

    // TODO: Добавить метод для расчета прибыли
    // TODO: Добавить метод для получения статистики продаж
    // TODO: Добавить метод для получения прибыли за период
    // TODO: Добавить метод для получения популярных товаров
    // TODO: Добавить метод для получения статистики по категориям
    // TODO: Добавить метод для экспорта данных о продажах
    // TODO: Добавить метод для анализа трендов продаж
} 