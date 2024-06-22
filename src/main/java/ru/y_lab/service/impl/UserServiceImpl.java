package ru.y_lab.service.impl;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;
import ru.y_lab.util.InputReader;
import ru.y_lab.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
public class UserServiceImpl implements UserService {

    private static final Scanner scanner = new Scanner(System.in);
    private final InputReader inputReader;
    private static final UserRepository userRepository = new UserRepository();
    private static final AuthenticationUtil authUtil = new AuthenticationUtil(userRepository);
    private static User currentUser = null;

    public UserServiceImpl(InputReader inputReader) {
        this.inputReader = inputReader;
        addAdminUser();
    }

    @Override
    public void registerUser() {
        System.out.print("Enter username: ");
        String username = inputReader.readLine();
        if (!ValidationUtil.validateUsername(username)) {
            System.out.println("Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores. Please try again.");
            return;
        }
        System.out.print("Enter password: ");
        String password = inputReader.readLine();
        if (!ValidationUtil.validatePassword(password)) {
            System.out.println("Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %. Please try again.");
            return;
        }

        String userId = UUID.randomUUID().toString();

        User user = new User(userId, username, password, "USER");
        userRepository.addUser(user);
        System.out.println("User registered successfully!");
    }

    @Override
    public void loginUser() throws UserNotFoundException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authUtil.authenticate(username, password)) {
            currentUser = userRepository.getUserByUsername(username);
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

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

    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.getUserById(userId);
    }

    private void addAdminUser() {
        User admin = new User(UUID.randomUUID().toString(),"Admin", "admin", "ADMIN");
        userRepository.addUser(admin);
    }
}