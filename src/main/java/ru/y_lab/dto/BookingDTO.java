package ru.y_lab.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookingDTO extends BaseDTO {
    private Long id;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}