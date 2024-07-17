package ru.y_lab.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI (Swagger) documentation.
 * This class configures the OpenAPI documentation for the Coworking Service API.
 */
@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
@OpenAPIDefinition
public class OpenApiConfiguration { }
