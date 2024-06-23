package ru.y_lab.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking with details such as ID, user ID, resource ID, start time, and end time.
 */
public class Booking {
    private String id;
    private String userId;
    private String resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Constructs a new Booking object with specified parameters.
     * @param id the booking ID
     * @param userId the user ID associated with the booking
     * @param resourceId the resource ID associated with the booking
     * @param startTime the start time of the booking
     * @param endTime the end time of the booking
     */
    public Booking(String id, String userId, String resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Gets the booking ID.
     * @return the booking ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the booking ID.
     * @param id the booking ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user ID associated with the booking.
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the booking.
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the resource ID associated with the booking.
     * @return the resource ID
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the resource ID associated with the booking.
     * @param resourceId the resource ID to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Gets the start time of the booking.
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the booking.
     * @param startTime the start time to set
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time of the booking.
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the booking.
     * @param endTime the end time to set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Compares this booking with another object for equality based on the booking ID.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    /**
     * Returns the hash code value for this booking based on the booking ID.
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the booking object.
     * @return a string representation containing booking details
     */
    @Override
    public String toString() {
        return String.format("%nBooking ID: %s%nUser ID: %s%nResource ID: %s%nStart time: %s%nEnd time: %s", id, userId, resourceId, startTime, endTime);
    }
}


