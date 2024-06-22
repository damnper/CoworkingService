package ru.y_lab.service;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.util.Scanner;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    void registerUser();

    void loginUser() throws UserNotFoundException;

    User getCurrentUser();

    void viewAllUsers();

    User getUserById(String userId) throws UserNotFoundException;
}
