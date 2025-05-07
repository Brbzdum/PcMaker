package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getByActive(boolean active) {
        return userRepository.findByActive(active);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // TODO: Добавить методы для смены пароля, активации аккаунта, проверки ролей и т.д.
} 