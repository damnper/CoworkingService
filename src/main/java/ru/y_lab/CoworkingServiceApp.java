package ru.y_lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * This class represents the main entry point for the CoworkingService application.
 *
 * @author Daler Yunusov
 * @version 1.0
 */
@SpringBootApplication
public class CoworkingServiceApp {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoworkingServiceApp.class, args);
    }
}