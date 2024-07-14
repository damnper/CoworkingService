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

    private final DatabaseManager databaseManager;

    /**
     * Initializes Liquibase by setting up the database connection,
     * creating the schema if it does not exist, and running the migrations.
     *
     * @return a message indicating the success of the initialization
     */
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

    /**
     * Creates the schema if it does not exist.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    private void createSchemaIfNotExists(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseSchema);
        }
    }

    /**
     * Runs the Liquibase update command to apply migrations.
     *
     * @param database the database object
     * @throws CommandExecutionException if an error occurs during command execution
     */
    private void update(Database database) throws CommandExecutionException {
        CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
        updateCommand.addArgumentValue("database", database);
        updateCommand.addArgumentValue("changelogFile", changelogFile);
        updateCommand.execute();
    }
}