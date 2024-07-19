package ru.y_lab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for setting up the web configuration.
 * This class configures the message converters for the application.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.y_lab")
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures the list of {@link HttpMessageConverter} to use for reading or writing to the body
     * of the request or response.
     *
     * @param converters the list of message converters to configure
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(builder.build());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(jackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter());
    }

    /**
     * Creates and returns the {@link ObjectMapper} bean.
     * This bean is used for JSON serialization and deserialization.
     *
     * @return a configured instance of {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}