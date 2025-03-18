package com.douglasmarq.imageservice.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Images Service API")
                                .description("API documentation for Images Service")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Your Name")
                                                .email("your.email@example.com")
                                                .url("https://douglasmarq.github.io")));
    }
}
