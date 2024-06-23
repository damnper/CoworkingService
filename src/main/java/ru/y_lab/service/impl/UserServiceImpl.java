package ru.y_lab.service.impl;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;
import ru.y_lab.util.InputReader;
import ru.y_lab.util.ValidationUtil;

import java.util.List;
import java.util.UUID;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
public class UserServiceImpl implements UserService {

    private final InputReader inputReader;
    private final UserRepository userRepository;
    private final AuthenticationUtil authUtil;
    private static User currentUser = null;

    /**
     * Constructs a UserServiceImpl object with the specified dependencies.
     * @param inputReader the input reader used for user interaction
     * @param userRepository the repository managing user data
     * @param addAdmin flag indicating whether to add an admin user during initialization
     */
    public UserServiceImpl(InputReader inputReader, UserRepository userRepository, boolean addAdmin) {
        this.inputReader = inputReader;
        this.userRepository = userRepository;
        this.authUtil = new AuthenticationUtil(userRepository);
        if (addAdmin) {
            addAdminUser();
        }
    }

    /**
     * Registers a new user by taking user input for username and password.
     * Validates the username and password format before registering the user.
     * @return the registered user, or null if registration fails
     */
    @Override
    public User registerUser() {
        System.out.print("Enter username: ");
        String username = inputReader.readLine();
        if (!ValidationUtil.validateUsername(username)) {
            System.out.println("Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores. Please try again.");
            return null;
        }
        System.out.print("Enter password: ");
        String password = inputReader.readLine();
        if (!ValidationUtil.validatePassword(password)) {
            System.out.println("Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %. Please try again.");
            return null;
        }

        String userId = UUID.randomUUID().toString();

        User user = new User(userId, username, password, "USER");
        User registeredUser = userRepository.addUser(user);
        System.out.println("User registered successfully!");
        return registeredUser;
    }

    /**
     * Logs in a user by authenticating with the provided username and password.
     * Sets the currentUser static field upon successful authentication.
     * @throws UserNotFoundException if the user with the provided username is not found
     */
    @Override
    public void loginUser() throws UserNotFoundException {
        System.out.print("Enter username: ");
        String username = inputReader.readLine();
        System.out.print("Enter password: ");
        String password = inputReader.readLine();

        if (authUtil.authenticate(username, password)) {
            currentUser = userRepository.getUserByUsername(username);
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
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

    /**
     * Displays information about all registered users.
     */
    @Override
    public void viewAllUsers() {
        int i = 1;
        List<User> users = userRepository.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            System.out.println("\n--- List of All Users ---");
            for (User user : users) {
                System.out.println(i + ") " + user.getId() + " " + user.getUsername());
                i++;
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
    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.getUserById(userId);
    }

    /**
     * Adds an admin user to the repository during initialization.
     */
    private void addAdminUser() {
        User admin = new User(UUID.randomUUID().toString(),"Admin", "admin", "ADMIN");
        userRepository.addUser(admin);
    }
}