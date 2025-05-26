package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.User;
import java.util.List;
import java.util.Optional;

// TODO: Репозиторий для пользователей
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByActivationCode(String activationCode);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByActiveTrue();
} 