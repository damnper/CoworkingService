package ru.y_lab.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class DatabaseConfig {

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.driver-class-name}")
    private String driverClassName;

}
