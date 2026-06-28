package io.github.meowpowpng.enterprisecoffee.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfiguration implements WebMvcConfigurer {

    private final ApplicationProperties properties;

    WebConfiguration(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(properties.allowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST");
    }
}
