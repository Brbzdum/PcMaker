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
                .addResourceLocations("file:uploads/");
    }
    
    /**
     * Настройка контроллеров представлений для маршрутов Vue.js
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Главная страница Vue.js приложения
        registry.addViewController("/").setViewName("forward:/index.html");
        
        // Обработка маршрутов Vue.js (все маршруты, кроме /api/** и /admin/**)
        registry.addViewController("/{path:^(?!api|admin).*$}/**").setViewName("forward:/index.html");
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
    public SpringTemplateEngine webTemplateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }
    
    /**
     * Конфигурация решателя представлений Thymeleaf
     */
    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine webTemplateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(webTemplateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setViewNames(new String[] {"**/admin/**"});
        return viewResolver;
    }
} 