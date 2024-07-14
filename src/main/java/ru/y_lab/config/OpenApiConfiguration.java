package ru.y_lab.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for setting up OpenAPI (Swagger) documentation.
 * This class configures the OpenAPI documentation for the Coworking Service API.
 */
@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
@OpenAPIDefinition
@Import({SpringDocConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.ui.SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class,
        JacksonAutoConfiguration.class})
public class OpenApiConfiguration {

    /**
     * Creates and returns the OpenAPI bean.
     * This bean configures the API documentation details such as title, version, and description.
     *
     * @return an {@link OpenAPI} instance with the configured API documentation details
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Coworking service API").version("v1"))
                .addSecurityItem(new SecurityRequirement().addList("sessionAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("sessionAuth",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("SESSION")));
    }
}
