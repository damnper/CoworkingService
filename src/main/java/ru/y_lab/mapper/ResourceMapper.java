package ru.y_lab.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "userId", target = "ownerId"),
            @Mapping(source = "name", target = "resourceName"),
            @Mapping(source = "type", target = "type")
    })
    ResourceDTO toDTO(Resource resource);

    @Mappings({
            @Mapping(source = "resource.id", target = "id"),
            @Mapping(source = "resource.name", target = "resourceName"),
            @Mapping(source = "resource.type", target = "resourceType"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "user.username", target = "ownerName")
    })
    ResourceWithOwnerDTO toResourceWithOwnerDTO(Resource resource, User user);
}
