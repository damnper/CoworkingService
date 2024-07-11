package ru.y_lab.config;

import lombok.Data;

@Data
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    public static DatabaseConfig load() {
        DatabaseConfig config = new DatabaseConfig();
        config.setUrl("jdbc:postgresql://localhost:5437/coworkingdb");
        config.setUsername("daler");
        config.setPassword("daler123");
        config.setDriverClassName("org.postgresql.Driver");
        return config;
    }
}
