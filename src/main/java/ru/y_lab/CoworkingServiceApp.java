package ru.y_lab;

import ru.y_lab.config.LiquibaseInitializer;

/**
 * The CoworkingServiceApp class serves as the main application entry point for managing coworking resources and bookings.
 */
public class CoworkingServiceApp {

    /**
     * Main method to start the Coworking Service application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        LiquibaseInitializer.initialize();

    }

}
