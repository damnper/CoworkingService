package ru.y_lab.util;

/**
 * The InputReader interface provides a contract for reading input.
 */
public interface InputReader {

    /**
     * Reads a line of input.
     * @return the line of input read
     */
    String readLine();

    /**
     * Reads and retrieves the user's choice from input.
     *
     * @return the user's choice as an integer
     */
    int getUserChoice();

    void close();
}
