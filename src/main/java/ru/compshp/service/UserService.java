package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.User;
import ru.compshp.repository.UserRepository;
import ru.compshp.repository.OrderRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public UserService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    // TODO: Добавить метод для создания пользователя
    // TODO: Добавить метод для обновления пользователя
    // TODO: Добавить метод для удаления пользователя
    // TODO: Добавить метод для получения пользователя по ID
    // TODO: Добавить метод для получения пользователя по email
    // TODO: Добавить метод для активации аккаунта
    // TODO: Добавить метод для смены пароля
    // TODO: Добавить метод для восстановления пароля
    // TODO: Добавить метод для проверки роли пользователя
    // TODO: Добавить метод для получения заказов пользователя
    // TODO: Добавить метод для получения корзины пользователя
    // TODO: Добавить метод для получения конфигураций пользователя
    // TODO: Добавить метод для получения отзывов пользователя
    // TODO: Добавить метод для получения истории действий
    // TODO: Добавить метод для управления уведомлениями
} 