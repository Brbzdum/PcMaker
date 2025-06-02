package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.repository.RoleRepository;

import java.util.Optional;

/**
 * Сервис для работы с ролями пользователей
 * Предоставляет методы для поиска ролей по имени
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Находит роль по имени
     * @param roleName имя роли из перечисления RoleName
     * @return Optional с найденной ролью или пустой Optional
     */
    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
} 