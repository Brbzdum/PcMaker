package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO: Добавить метод для проверки возможности редактирования
    // TODO: Добавить метод для проверки возможности удаления
    // TODO: Добавить метод для обновления рейтинга продукта
    // TODO: Добавить метод для валидации рейтинга
    // TODO: Добавить метод для получения всех отзывов пользователя
    // TODO: Добавить метод для получения всех отзывов продукта
    // TODO: Добавить метод для расчета среднего рейтинга
} 