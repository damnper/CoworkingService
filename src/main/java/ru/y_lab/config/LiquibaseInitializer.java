package ru.y_lab.config;

import jakarta.annotation.PostConstruct;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CommandExecutionException;
import liquibase.exception.LiquibaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@RequiredArgsConstructor
public class LiquibaseInitializer {

    @Value("${liquibase.change-log-file}")
    private String changelogFile;

    @Value("${liquibase.default-schema-name}")
    private String defaultSchema;

    @Value("${liquibase.liquibase-schema-name}")
    private String liquibaseSchema;

    private final DatabaseManager databaseManager;

    @PostConstruct
    public String initializeLiquibase() {
        try (Connection connection = databaseManager.getConnection()) {
            createSchemaIfNotExists(connection);

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setLiquibaseSchemaName(liquibaseSchema);
            database.setDefaultSchemaName(defaultSchema);

            update(database);
        } catch (SQLException | LiquibaseException e) {
            System.err.println("SQL Exception in migrations: " + e.getMessage());
        }
        return "Liquibase initialized successfully";
    }

    private void createSchemaIfNotExists(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseSchema);
        }
    }

    private void update(Database database) throws CommandExecutionException {
        CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
        updateCommand.addArgumentValue("database", database);
        updateCommand.addArgumentValue("changelogFile", changelogFile);
        updateCommand.execute();
    }
}