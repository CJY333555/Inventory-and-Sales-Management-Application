package com.techshop.inventorypos;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring Boot is used here purely as an IoC container + Spring Data JPA engine.
 * We exclude the web server since this is a desktop app, not a web app.
 */
@SpringBootApplication(exclude = WebMvcAutoConfiguration.class)
public class InventoryPosApplication {

    public static ConfigurableApplicationContext run(String[] args) {
        return new SpringApplicationBuilder(InventoryPosApplication.class)
                .web(org.springframework.boot.WebApplicationType.NONE)
                .run(args);
    }
}
