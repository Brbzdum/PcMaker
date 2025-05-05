package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.User;

// TODO: Репозиторий для пользователей
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: Методы поиска по email, имени и активности
} 