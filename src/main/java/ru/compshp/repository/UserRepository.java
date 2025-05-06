package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.User;
import java.util.Optional;
import java.util.List;

// TODO: Репозиторий для пользователей
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByActive(boolean active);
    // Можно добавить другие методы поиска по необходимости
} 