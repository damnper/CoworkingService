package ru.y_lab.util;

import java.util.Scanner;

/**
 * Implementation of InputReader that reads input from the console using Scanner.
 */
public class ConsoleInputReader implements InputReader {
    private final Scanner scanner;

    /**
     * Constructs a ConsoleInputReader using System.in for input.
     * Initializes a new Scanner object with System.in.
     */
    public ConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a line of input from the console.
     * @return the line of input read from the console
     */
    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Reads and retrieves the user's choice from input.
     *
     * @return the user's choice as an integer
     */
    @Override
    public int getUserChoice() {
        try {
            return Integer.parseInt(readLine());
        } catch (NumberFormatException e) {
            return -1; // Return -1 for any parsing errors
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}