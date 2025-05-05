package ru.compshp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // TODO: Добавить загрузку пользователя по email
        // TODO: Добавить проверку активности пользователя
        // TODO: Добавить проверку ролей пользователя
        // TODO: Добавить обработку исключений
        return null;
    }

    // TODO: Добавить метод для проверки прав доступа
    // TODO: Добавить метод для получения ролей пользователя
    // TODO: Добавить метод для проверки активности пользователя
    // TODO: Добавить метод для проверки блокировки пользователя
    // TODO: Добавить метод для проверки срока действия пароля
    // TODO: Добавить метод для проверки необходимости смены пароля
    // TODO: Добавить метод для проверки попыток входа
    // TODO: Добавить метод для блокировки пользователя
    // TODO: Добавить метод для разблокировки пользователя
} 