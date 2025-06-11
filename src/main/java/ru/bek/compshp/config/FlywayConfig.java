package ru.bek.compshp.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Конфигурация Flyway для управления миграциями
 * Этот класс гарантирует, что миграции выполняются до инициализации данных
 */
@Configuration
public class FlywayConfig {

    @Autowired
    private Environment env;

    /**
     * Настраивает и запускает Flyway миграции
     * Важно: этот bean создается перед DataInitializer благодаря @Bean аннотации
     */
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(Boolean.parseBoolean(env.getProperty("spring.flyway.baseline-on-migrate", "true")))
                .outOfOrder(Boolean.parseBoolean(env.getProperty("spring.flyway.out-of-order", "true")))
                .validateOnMigrate(Boolean.parseBoolean(env.getProperty("spring.flyway.validate-on-migrate", "false")))
                .locations(env.getProperty("spring.flyway.locations", "classpath:db/migration"))
                .load();
    }
} 