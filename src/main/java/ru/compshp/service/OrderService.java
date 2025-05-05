package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.Order;
import ru.compshp.repository.OrderRepository;
import ru.compshp.repository.ProductRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // TODO: Добавить метод для создания заказа
    // TODO: Добавить метод для обновления заказа
    // TODO: Добавить метод для отмены заказа
    // TODO: Добавить метод для получения заказа по ID
    // TODO: Добавить метод для получения всех заказов пользователя
    // TODO: Добавить метод для изменения статуса заказа
    // TODO: Добавить метод для расчета стоимости заказа
    // TODO: Добавить метод для проверки наличия товаров
    // TODO: Добавить метод для применения скидок
    // TODO: Добавить метод для получения истории статусов
    // TODO: Добавить метод для получения статистики заказов
    // TODO: Добавить метод для экспорта данных о заказах
    // TODO: Добавить метод для получения популярных товаров
    // TODO: Добавить метод для получения прибыли
    // TODO: Добавить метод для получения трендов заказов
} 