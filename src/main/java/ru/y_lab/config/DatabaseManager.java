package ru.y_lab.config;

import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections using the provided configuration.
 * This class is responsible for setting up the database configuration
 * and establishing connections to the database.
 */
@Configuration
public class DatabaseManager {

    private DatabaseConfig dbConfig;

    /**
     * Establishes a connection to the database using the configured properties.
     *
     * @return a {@link Connection} to the database
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }

    /**
     * Sets the database configuration and loads the database driver class.
     *
     * @param config the {@link DatabaseConfig} containing database connection properties
     * @throws RuntimeException if the database driver class cannot be loaded
     */
    public void setConfig(DatabaseConfig config) {
        this.dbConfig = config;
        try {
            Class.forName(dbConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }
}
