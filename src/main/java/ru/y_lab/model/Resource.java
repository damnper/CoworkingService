package ru.y_lab.model;

import java.util.Objects;

/**
 * Represents a resource with details such as ID, user ID, name, and type.
 */
public class Resource {
    private String id;
    private String userId;
    private String name;
    private String type;

    /**
     * Constructs a new Resource object with specified parameters.
     * @param id the resource ID
     * @param userId the user ID associated with the resource
     * @param name the name of the resource
     * @param type the type of the resource
     */
    public Resource(String id, String userId, String name, String type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the resource ID.
     * @return the resource ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the resource ID.
     * @param id the resource ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user ID associated with the resource.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the resource.
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the resource.
     * @return the name of the resource
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the resource.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the resource.
     * @return the type of the resource
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the resource.
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Compares this resource with another object for equality based on the resource ID.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id);
    }

    /**
     * Returns the hash code value for this resource based on the resource ID.
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the resource object.
     * @return a string representation containing resource details
     */
    @Override
    public String toString() {
        return String.format("%nResource ID: %s%nUser ID: %s%nName: %s%nType: %s", id, userId, name, type);
    }
}


