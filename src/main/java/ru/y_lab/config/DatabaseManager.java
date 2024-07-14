package ru.y_lab.config;

import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DatabaseManager {

    private DatabaseConfig dbConfig;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }

    public void setConfig(DatabaseConfig config) {
        this.dbConfig = config;
        try {
            Class.forName(dbConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }
}
