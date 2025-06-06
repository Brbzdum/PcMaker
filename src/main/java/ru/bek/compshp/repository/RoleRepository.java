package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.enums.RoleName;

import java.util.Optional;

/**
 * Репозиторий для работы с ролями
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Находит роль по имени
     * @param name имя роли
     * @return Optional с ролью
     */
    Optional<Role> findByName(RoleName name);
} 