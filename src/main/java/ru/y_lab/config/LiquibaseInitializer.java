package ru.y_lab.config;

import jakarta.annotation.PostConstruct;
import liquibase.exception.DatabaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes Liquibase for database migrations.
 * This class is responsible for setting up Liquibase with the configured properties,
 * creating necessary schemas if they do not exist, and running the migrations.
 */
@Configuration
@RequiredArgsConstructor
public class LiquibaseInitializer {

    @Value("${liquibase.change-log-file}")
    private String changelogFile;

    @Value("${liquibase.default-schema-name}")
    private String defaultSchema;

    @Value("${liquibase.liquibase-schema-name}")
    private String liquibaseSchema;

    private final DataSource dataSource;

    @PostConstruct
    public void init() throws DatabaseException {
        createSchemaIfNotExists();
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changelogFile);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        return liquibase;
    }

    private void createSchemaIfNotExists() throws DatabaseException {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseSchema);
        } catch (SQLException e) {
            throw new DatabaseException("Error creating schema");
        }
    }
}