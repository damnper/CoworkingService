package ru.y_lab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a booking with details such as ID, user ID, resource ID, start time, and end time.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    private String id;

    private String userId;

    private String resourceId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}


