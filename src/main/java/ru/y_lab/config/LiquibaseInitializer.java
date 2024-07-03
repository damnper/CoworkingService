package ru.y_lab.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LiquibaseInitializer {

    private static final String CHANGELOG_FILE = "db/changelog.xml";
    private static final String DEFAULT_SCHEMA = "coworking_service";
    private static final String LIQUIBASE_SCHEMA = "liquibase";

    public static void initialize() {
        Connection connection = null;
        Liquibase liquibase = null;

        try {
            connection = DatabaseManager.getConnection();
            createSchemaIfNotExists(connection);

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(DEFAULT_SCHEMA);
            database.setLiquibaseSchemaName(LIQUIBASE_SCHEMA);

            liquibase = new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.out.println("SQL Exception in migrations: " + e.getMessage());
        } finally {
            if (liquibase != null) {
                try {
                    liquibase.close();
                } catch (Exception e) {
                    System.out.println("Error during closing liquibase: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("Error during closing connection: " + e.getMessage());
                }
            }
        }
    }

    private static void createSchemaIfNotExists(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + LIQUIBASE_SCHEMA);
        }
    }
}