package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по имени пользователя
     * @param username имя пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Находит пользователя по email
     * @param email email пользователя
     * @return Optional с пользователем
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Находит пользователя по токену верификации
     * @param token токен верификации
     * @return Optional с пользователем
     */
    Optional<User> findByVerificationToken(String token);
    
    /**
     * Проверяет существование пользователя с заданным именем
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);
    
    /**
     * Проверяет существование пользователя с заданным email
     * @param email email пользователя
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);
    
    /**
     * Находит пользователя по коду активации
     * @param activationCode код активации
     * @return Optional с пользователем
     */
    Optional<User> findByActivationCode(String activationCode);
    
    /**
     * Находит пользователя по токену сброса пароля
     * @param resetToken токен сброса пароля
     * @return Optional с пользователем
     */
    Optional<User> findByResetToken(String resetToken);
    
    /**
     * Считает количество пользователей, созданных после указанной даты
     * @param date дата
     * @return количество пользователей
     */
    long countByCreatedAtAfter(LocalDateTime date);

    /**
     * Находит пользователей с активным статусом
     * @return список активных пользователей
     */
    List<User> findByActiveTrue();

    /**
     * Поиск пользователей по части имени или email
     * @param query поисковый запрос
     * @return список пользователей
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findByUsernameOrEmailContaining(@Param("query") String query);
    
    /**
     * Находит пользователей, чей токен верификации истек
     * @param date дата истечения токена
     * @return список пользователей
     */
    List<User> findByTokenExpiryDateBeforeAndEmailVerifiedFalse(LocalDateTime date);

    /**
     * Находит пользователей с заданным активным статусом
     * @param active активный статус
     * @return список пользователей
     */
    List<User> findByActive(Boolean active);

    /**
     * Находит пользователей, созданных после указанной даты
     * @param date дата
     * @return список пользователей
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
} 