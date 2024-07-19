package ru.y_lab.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the database connection.
 * This class holds the properties needed to configure the datasource.
 */
@Data
@Configuration
public class DatabaseConfig {

    /**
     * The URL of the datasource.
     */
    @Value("${datasource.url}")
    private String url;

    /**
     * The username used to connect to the datasource.
     */
    @Value("${datasource.username}")
    private String username;

    /**
     * The password used to connect to the datasource.
     */
    @Value("${datasource.password}")
    private String password;

    /**
     * The driver class name for the datasource.
     */
    @Value("${datasource.driver-class-name}")
    private String driverClassName;

}
