package ru.bek.compshp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация Spring MVC
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Настройка обработчиков статических ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Общие статические ресурсы
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
                
        // Статические ресурсы для админки
        registry.addResourceHandler("/admin-static/**")
                .addResourceLocations("classpath:/static/admin/");
                
        // Папка для загруженных изображений
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
} 