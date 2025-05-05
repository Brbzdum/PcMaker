package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Review;

// TODO: Репозиторий для отзывов
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // TODO: Методы поиска по товару и пользователю
} 