package ru.y_lab.model;

import java.util.Objects;

/**
 * Represents a user with details such as ID, username, password, and role.
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String role;

    /**
     * Constructs a new User object with specified parameters.
     * @param id the user ID
     * @param username the username of the user
     * @param password the password of the user
     * @param role the role of the user
     */
    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * @param id the user ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the role of the user.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Compares this user with another object for equality based on the user ID.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Returns the hash code value for this user based on the user ID.
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the user object.
     * @return a string representation containing user details
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

