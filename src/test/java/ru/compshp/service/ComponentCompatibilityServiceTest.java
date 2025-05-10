package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.CompatibilityRuleRepository;
import ru.compshp.repository.ProductCompatibilityRepository;
import ru.compshp.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Тесты для сервиса проверки совместимости компонентов
 */
@SpringBootTest
class ComponentCompatibilityServiceTest {

    @MockBean
    private CompatibilityRuleRepository ruleRepository;

    @MockBean
    private ProductCompatibilityRepository compatibilityRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductService productService;

    private ComponentCompatibilityService compatibilityService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Инициализация сервиса и необходимых зависимостей
        compatibilityService = new ComponentCompatibilityService(
                ruleRepository,
                compatibilityRepository,
                productRepository,
                productService
        );
        objectMapper = new ObjectMapper();
    }

    /**
     * Тест успешной сборки с совместимыми компонентами
     * Проверяет корректную работу с CPU, материнской платой и блоком питания
     */
    @Test
    void testSuccessfulBuildWithCompatibleComponents() throws Exception {
        // Подготовка тестовых данных
        Product cpu = createProduct(ComponentType.CPU, Map.of(
                "socket", "LGA1700",
                "tdp", 125
        ));
        Product motherboard = createProduct(ComponentType.MOTHERBOARD, Map.of(
                "socket", "LGA1700",
                "formFactor", "ATX"
        ));
        Product psu = createProduct(ComponentType.PSU, Map.of(
                "wattage", 750,
                "efficiency", "80+ Gold"
        ));

        // Выполнение проверки совместимости
        boolean isCompatible = compatibilityService.checkConfigurationCompatibility(
                cpu, Arrays.asList(motherboard, psu));

        // Проверка результатов
        assertTrue(isCompatible, "Компоненты должны быть совместимы");
    }

    /**
     * Тест ошибки при несовпадении сокетов
     * Проверяет корректное определение несовместимости CPU и материнской платы
     */
    @Test
    void testSocketMismatchError() throws Exception {
        // Подготовка тестовых данных с несовместимыми сокетами
        Product cpu = createProduct(ComponentType.CPU, Map.of(
                "socket", "AM4",
                "tdp", 105
        ));
        Product motherboard = createProduct(ComponentType.MOTHERBOARD, Map.of(
                "socket", "LGA1700",
                "formFactor", "ATX"
        ));

        // Выполнение проверки совместимости
        boolean isCompatible = compatibilityService.checkConfigurationCompatibility(
                cpu, List.of(motherboard));

        // Проверка результатов
        assertFalse(isCompatible, "Компоненты с разными сокетами не должны быть совместимы");
    }

    /**
     * Тест предупреждения о пограничной мощности БП
     * Проверяет ситуацию, когда мощность БП находится на грани допустимого
     */
    @Test
    void testBorderlinePSUPowerWarning() throws Exception {
        // Подготовка тестовых данных с высоким энергопотреблением
        Product cpu = createProduct(ComponentType.CPU, Map.of(
                "socket", "LGA1700",
                "tdp", 250
        ));
        Product gpu = createProduct(ComponentType.GPU, Map.of(
                "powerConsumption", 350,
                "length", 300
        ));
        Product psu = createProduct(ComponentType.PSU, Map.of(
                "wattage", 650,
                "efficiency", "80+ Bronze"
        ));

        // Выполнение проверки совместимости
        boolean isCompatible = compatibilityService.checkConfigurationCompatibility(
                cpu, Arrays.asList(gpu, psu));

        // Проверка результатов
        assertTrue(isCompatible, "Компоненты должны быть технически совместимы");
        // Примечание: В реальном приложении здесь должна быть система предупреждений
        // для уведомления пользователей о пограничных ситуациях с мощностью БП
    }

    /**
     * Вспомогательный метод для создания тестовых продуктов
     * @param type тип компонента
     * @param specs спецификации компонента
     * @return созданный продукт
     */
    private Product createProduct(ComponentType type, Map<String, Object> specs) throws Exception {
        Product product = new Product();
        product.setComponentType(type);
        product.setSpecs(objectMapper.writeValueAsString(specs));
        return product;
    }
} 