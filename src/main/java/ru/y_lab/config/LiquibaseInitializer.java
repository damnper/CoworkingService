package ru.y_lab.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class LiquibaseInitializer {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) throws SQLException {
        createSchemaIfNotExists(dataSource);

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog.xml");
        liquibase.setDefaultSchema("coworking_service");
        liquibase.setLiquibaseSchema("liquibase");
        liquibase.setShouldRun(true);
        return liquibase;
    }

    private void createSchemaIfNotExists(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS liquibase");
        }
    }
}