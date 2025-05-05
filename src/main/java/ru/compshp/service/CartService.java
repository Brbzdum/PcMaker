package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.Cart;
import ru.compshp.model.Product;
import ru.compshp.repository.CartRepository;
import ru.compshp.repository.ProductRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // TODO: Добавить метод для получения корзины пользователя
    // TODO: Добавить метод для создания корзины
    // TODO: Добавить метод для добавления товара в корзину
    // TODO: Добавить метод для обновления количества товара
    // TODO: Добавить метод для удаления товара из корзины
    // TODO: Добавить метод для очистки корзины
    // TODO: Добавить метод для расчета стоимости корзины
    // TODO: Добавить метод для проверки наличия товаров
    // TODO: Добавить метод для применения скидок
    // TODO: Добавить метод для сохранения корзины
    // TODO: Добавить метод для восстановления корзины
    // TODO: Добавить метод для получения рекомендуемых товаров
    // TODO: Добавить метод для проверки совместимости компонентов
    // TODO: Добавить метод для создания заказа из корзины
} 