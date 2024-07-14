package ru.y_lab.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coworking Service API")
                        .version("3.0.0")
                        .description("API documentation for the Coworking Service"));
    }

}
