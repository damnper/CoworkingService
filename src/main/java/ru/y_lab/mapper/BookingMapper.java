package ru.y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.y_lab.dto.BookingDTO;
import ru.y_lab.dto.BookingWithOwnerResourceDTO;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;

/**
 * Mapper interface for converting between Booking entities and DTOs.
 * This interface uses MapStruct for automatic mapping.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Converts a Booking entity to a BookingDTO.
     *
     * @param booking the Booking entity to convert
     * @return the converted BookingDTO
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "resourceId", target = "resourceId"),
            @Mapping(source = "startTime", target = "startTime"),
            @Mapping(source = "endTime", target = "endTime")
    })
    BookingDTO toDTO(Booking booking);


    /**
     * Converts a Booking entity along with its associated Resource and User entities to a BookingWithOwnerResourceDTO.
     *
     * @param booking the Booking entity to convert
     * @param resource the associated Resource entity
     * @param user the associated User entity
     * @return the converted BookingWithOwnerResourceDTO
     */
    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "resource.id", target = "resourceId"),
            @Mapping(source = "booking.id", target = "bookingId"),
            @Mapping(source = "user.username", target = "ownerName"),
            @Mapping(source = "resource.name", target = "resourceName"),
            @Mapping(source = "resource.type", target = "resourceType"),
            @Mapping(target = "date", expression = "java(booking.getStartTime().toLocalDate().toString())"),
            @Mapping(target = "startTime", expression = "java(booking.getStartTime().toLocalTime().toString())"),
            @Mapping(target = "endTime", expression = "java(booking.getEndTime().toLocalTime().toString())")
    })
    BookingWithOwnerResourceDTO toBookingWithOwnerResourceDTO(Booking booking, Resource resource, User user);
}
