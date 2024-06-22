package ru.y_lab.model;

import java.util.Objects;

public class Resource {
    private String id;
    private String userId;
    private String name;
    private String type;

    public Resource(String id, String userId, String name, String type) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%nResource ID: %s%nUser ID: %s%nName: %s%nType: %s", id, userId, name, type);
    }
}

