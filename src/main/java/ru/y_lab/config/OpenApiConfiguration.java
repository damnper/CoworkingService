package ru.y_lab.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI (Swagger) documentation.
 * This class configures the OpenAPI documentation for the Coworking Service API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Coworking Service API",
                version = "1.0",
                description = "API documentation for Coworking Service",
                license = @License(name = "Apache 2.0", url = "https://springdoc.org")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfiguration {

    /**
     * Creates and returns the OpenAPI bean for user-related endpoints.
     * This bean configures the API documentation for user-related operations.
     *
     * @return a {@link GroupedOpenApi} instance with the configured API documentation details
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    /**
     * Creates and returns the OpenAPI bean for resource-related endpoints.
     * This bean configures the API documentation for resource-related operations.
     *
     * @return a {@link GroupedOpenApi} instance with the configured API documentation details
     */
    @Bean
    public GroupedOpenApi resourceApi() {
        return GroupedOpenApi.builder()
                .group("resources")
                .pathsToMatch("/api/v1/resources/**")
                .build();
    }

    /**
     * Creates and returns the OpenAPI bean for booking-related endpoints.
     * This bean configures the API documentation for booking-related operations.
     *
     * @return a {@link GroupedOpenApi} instance with the configured API documentation details
     */
    @Bean
    public GroupedOpenApi bookingsApi() {
        return GroupedOpenApi.builder()
                .group("bookings")
                .pathsToMatch("/api/v1/bookings/**")
                .build();
    }
}
