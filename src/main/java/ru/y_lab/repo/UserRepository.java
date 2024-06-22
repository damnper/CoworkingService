package ru.y_lab.repo;


import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.util.*;

/**
 * The UserRepository class provides methods to manage users.
 * It stores user data in a HashMap and supports CRUD operations.
 */
public class UserRepository {

    private final Map<String, User> users = new HashMap<>();

    /**
     * Adds a new user to the repository.
     * @param user the user to be added
     */
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Retrieves a user by their ID.
     * @param id the ID of the user
     * @return the user with the specified ID, or null if not found
     */
    public User getUserById(String id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user
     * @return the user with the specified username, or null if not found
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        return optionalUser.get();
    }

    /**
     * Updates an existing user in the repository.
     * @param user the user with updated information
     */
    public void updateUser(User user) throws UserNotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User with ID " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
    }

    /**
     * Deletes a user from the repository by their ID.
     * @param id the ID of the user to be deleted
     */
    public void deleteUser(String id) throws UserNotFoundException {
        if (users.remove(id) == null) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
