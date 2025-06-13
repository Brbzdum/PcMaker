package ru.bek.compshp.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.bek.compshp.model.Category;
import ru.bek.compshp.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Класс для настройки категорий периферии
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test") // Не запускать в тестовом профиле
public class PeripheralCategorySetup {

    private final CategoryRepository categoryRepository;

    /**
     * Метод для настройки категорий периферии после запуска приложения
     */
    @PostConstruct
    public void setupPeripheralCategories() {
        log.info("Настройка категорий периферии...");
        
        // Список slug'ов категорий периферии
        List<String> peripheralSlugs = Arrays.asList(
            "keyboard", "mouse", "monitor", "headset", "speakers", "mousepad", "microphone"
        );
        
        // Словарь для перевода slug в название категории, если категории не существуют
        Map<String, String> slugToName = Map.of(
            "keyboard", "Клавиатуры",
            "mouse", "Мыши",
            "monitor", "Мониторы",
            "headset", "Наушники",
            "speakers", "Колонки",
            "mousepad", "Коврики для мыши",
            "microphone", "Микрофоны"
        );
        
        // Для каждого slug'а находим категорию и устанавливаем флаг isPeripheral
        for (String slug : peripheralSlugs) {
            // Сначала ищем по slug
            Optional<Category> categoryBySlug = categoryRepository.findBySlug(slug);
            // Затем ищем по имени
            Optional<Category> categoryByName = categoryRepository.findByName(slugToName.getOrDefault(slug, slug));
            
            Category category = null;
            
            if (categoryBySlug.isPresent()) {
                // Если нашли по slug
                category = categoryBySlug.get();
                log.info("Найдена категория по slug: '{}'", category.getName());
            } else if (categoryByName.isPresent()) {
                // Если нашли по имени
                category = categoryByName.get();
                log.info("Найдена категория по имени: '{}'", category.getName());
            } else {
                // Если категория не существует, создаем ее
                log.info("Категория '{}' не найдена, создаем...", slugToName.getOrDefault(slug, slug));
                category = Category.builder()
                        .name(slugToName.getOrDefault(slug, slug))
                        .slug(slug)
                        .isPeripheral(true)
                        .isPcComponent(false)
                        .build();
            }
            
            // Устанавливаем флаг isPeripheral
            category.setIsPeripheral(true);
            
            categoryRepository.save(category);
            log.info("Категория '{}' сохранена с флагом isPeripheral=true", category.getName());
        }
        
        log.info("Настройка категорий периферии завершена");
    }
} 