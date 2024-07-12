package ru.y_lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "role", target = "role")
    })
    UserDTO toDTO(User user);
}
