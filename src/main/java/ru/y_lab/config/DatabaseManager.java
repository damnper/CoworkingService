package ru.y_lab.config;

import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
public class DatabaseManager {
    private static DatabaseConfig dbConfig = DatabaseConfig.load();

    static {
        try {
            Class.forName(dbConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }

    // Метод для установки кастомной конфигурации
    public static void setConfig(DatabaseConfig config) {
        dbConfig = config;
        try {
            Class.forName(dbConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }
}
