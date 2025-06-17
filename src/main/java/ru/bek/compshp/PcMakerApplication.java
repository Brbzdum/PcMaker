package ru.bek.compshp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.bek.compshp.config.FlywayConfig;

@SpringBootApplication
@Import(FlywayConfig.class)
public class PcMakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcMakerApplication.class, args);
    }

}
