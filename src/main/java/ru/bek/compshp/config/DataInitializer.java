package ru.bek.compshp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.bek.compshp.model.Role;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.enums.RoleName;
import ru.bek.compshp.repository.RoleRepository;
import ru.bek.compshp.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Класс для инициализации тестовых данных и администратора
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем администратора если его еще нет
        createAdminIfNotExists();
    }

    /**
     * Создает администратора в базе данных, если его нет
     */
    private void createAdminIfNotExists() {
        // Проверяем, есть ли роль ADMIN в базе
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.ROLE_ADMIN);
                    return roleRepository.save(newRole);
                });

        // Проверяем, есть ли пользователь admin в базе
        Optional<User> adminUser = userRepository.findByUsername("admin");
        
        if (adminUser.isEmpty()) {
            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setEmail("admin@pcmaker.com");
            newAdmin.setPassword(passwordEncoder.encode("admin"));
            newAdmin.setName("Администратор");
            newAdmin.setActive(true);
            newAdmin.setRoles(Set.of(adminRole));
            
            userRepository.save(newAdmin);
            
            System.out.println("Администратор создан: admin/admin");
        }
    }
} 