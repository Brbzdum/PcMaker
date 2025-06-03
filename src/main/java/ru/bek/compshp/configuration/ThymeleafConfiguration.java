package ru.bek.compshp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Конфигурация Thymeleaf
 */
@Configuration
public class ThymeleafConfiguration {

    /**
     * Создаем и настраиваем Thymeleaf TemplateResolver
     * @return настроенный шаблонный резолвер
     */
    @Bean
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // Отключаем кэширование для разработки
        return resolver;
    }

    /**
     * Создаем и настраиваем Thymeleaf TemplateEngine
     * @param templateResolver резолвер шаблонов
     * @return настроенный движок шаблонов
     */
    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setEnableSpringELCompiler(true); // Включаем компиляцию выражений SpringEL
        return engine;
    }

    /**
     * Создаем и настраиваем ViewResolver для Thymeleaf
     * @param templateEngine движок шаблонов
     * @return настроенный резолвер представлений
     */
    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
} 