package ru.bek.compshp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.springframework.format.FormatterRegistry;
import ru.bek.compshp.util.StringToMapConverter;

/**
 * Конфигурация веб-интерфейса с поддержкой Thymeleaf для админ-панели
 * и статическими ресурсами для Vue.js фронтенда в основном приложении
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "https://pcmaker.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Настройка обработчиков статических ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Обработчик для статических ресурсов админ-панели
        registry.addResourceHandler("/admin-static/**")
                .addResourceLocations("classpath:/static/admin/");
        
        // Обработчик для статических ресурсов основного приложения
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // Обработчик для загруженных изображений
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
        
        // Обработчики для корневых статических файлов (по каждому типу файла отдельно)
        registry.addResourceHandler("/*.js")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.css")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.ico")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.png")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.jpg")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.gif")
                .addResourceLocations("classpath:/static/");
    }
    
    /**
     * Настройка контроллеров представлений для маршрутов приложения
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Перенаправляем корневой путь на страницу входа
        registry.addViewController("/").setViewName("redirect:/login");
        
        // Перенаправляем все другие пути (кроме API, admin, static, uploads, login)
        // на страницу входа
        registry.addViewController("/{path:^(?!api|admin|static|uploads|login).*$}/**")
                .setViewName("redirect:/login");
    }
    
    /**
     * Конфигурация решателя шаблонов Thymeleaf
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false); // В продакшене должно быть true
        return templateResolver;
    }
    
    /**
     * Конфигурация движка шаблонов Thymeleaf
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }
    
    /**
     * Конфигурация решателя представлений Thymeleaf
     */
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setViewNames(new String[] {"*"});
        return resolver;
    }

    /**
     * Регистрация форматтеров и конвертеров
     * @param registry реестр форматтеров
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Добавляем конвертер для преобразования строки с характеристиками в Map
        registry.addConverter(new StringToMapConverter());
    }
} 