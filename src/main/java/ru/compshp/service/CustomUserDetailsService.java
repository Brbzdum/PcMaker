package ru.compshp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;
import ru.compshp.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        
        return new CustomUserDetails(user);
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