package ru.y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.model.User;

/**
 * Mapper interface for converting between User entities and DTOs.
 * This interface uses MapStruct for automatic mapping.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert
     * @return the converted UserDTO
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "role", target = "role")
    })
    UserDTO toDTO(User user);
}
