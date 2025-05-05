package ru.compshp.service;

import org.springframework.stereotype.Service;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.repository.PCConfigurationRepository;
import ru.compshp.repository.ProductRepository;

@Service
public class PCConfigurationService {
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ProductRepository productRepository;

    public PCConfigurationService(PCConfigurationRepository pcConfigurationRepository,
                                ProductRepository productRepository) {
        this.pcConfigurationRepository = pcConfigurationRepository;
        this.productRepository = productRepository;
    }

    // TODO: Добавить метод для создания конфигурации
    // TODO: Добавить метод для обновления конфигурации
    // TODO: Добавить метод для удаления конфигурации
    // TODO: Добавить метод для получения конфигурации по ID
    // TODO: Добавить метод для получения всех конфигураций пользователя
    // TODO: Добавить метод для добавления компонента
    // TODO: Добавить метод для удаления компонента
    // TODO: Добавить метод для проверки совместимости компонентов
    // TODO: Добавить метод для расчета стоимости конфигурации
    // TODO: Добавить метод для клонирования конфигурации
    // TODO: Добавить метод для экспорта конфигурации
    // TODO: Добавить метод для проверки наличия компонентов
    // TODO: Добавить метод для получения рекомендуемых компонентов
    // TODO: Добавить метод для расчета энергопотребления
} 