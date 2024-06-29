package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;
import ru.y_lab.util.InputReader;
import ru.y_lab.util.ValidationUtil;

import java.util.List;

import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InputReader inputReader;
    private final UserRepository userRepository;
    private final AuthenticationUtil authUtil;
    private static User currentUser = null;

    /**
     * Registers a new user by taking user input for username and password.
     * Validates the username and password format before registering the user.
     * @return the registered user, or null if registration fails
     */
    @Override
    public User registerUser() {
        System.out.print(CYAN + "Enter username: " + RESET);
        String username = inputReader.readLine();
        if (!ValidationUtil.validateUsername(username)) {
            System.out.println(RED + "Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores. Please try again." + RESET);
            return null;
        }

        System.out.print(CYAN + "Enter password: " + RESET);
        String password = inputReader.readLine();
        if (!ValidationUtil.validatePassword(password)) {
            System.out.println(RED + "Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %. Please try again." + RESET);
            return null;
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .role("USER")
                .build();

        User registeredUser = userRepository.addUser(user);
        System.out.println(GREEN + "\nUser registered successfully!" + RESET);
        return registeredUser;
    }

    /**
     * Logs in a user by authenticating with the provided username and password.
     * Sets the currentUser static field upon successful authentication.
     * @throws UserNotFoundException if the user with the provided username is not found
     */
    @Override
    public void loginUser() throws UserNotFoundException {
        System.out.print(CYAN + "Enter username: " + RESET);
        String username = inputReader.readLine();
        System.out.print(CYAN + "Enter password: " + RESET);
        String password = inputReader.readLine();

        if (authUtil.authenticate(username, password)) {
            currentUser = userRepository.getUserByUsername(username);
            System.out.println(GREEN + "\nLogin successful!" + RESET);
        } else {
            System.out.println(RED + "\nInvalid credentials. Please try again." + RESET);
        }
    }

    /**
     * Displays a list of all users retrieved from the user repository.
     * If no users are found, it prints a message indicating that no users are available.
     * Otherwise, it prints the ID and username of each user.
     */
    @Override
    public void viewAllUsers() {
        List<User> users = userRepository.getAllUsers();
        if (users.isEmpty()) {
            System.out.println(YELLOW + "\nNo users available." + RESET);
        } else {
            System.out.println(BLUE + "\n--- List of All Users ---\n" + RESET);
            System.out.println(CYAN + "----------------------------" + RESET);
            for (User user : users) {
                System.out.println(PURPLE + user.getId() + " " + user.getUsername() + RESET);
                System.out.println(CYAN + "----------------------------" + RESET);
            }
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws UserNotFoundException if the user with the given ID is not found
     */
    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        return userRepository.getUserById(userId);
    }

    /**
     * Retrieves the currently logged-in user.
     * @return the current user, or null if no user is currently logged in
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current logged-in user.
     * @param user the user to set as the current user
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }
}