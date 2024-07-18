package ru.y_lab.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;

/**
 * Mapper interface for converting between Resource entities and DTOs.
 * This interface uses MapStruct for automatic mapping.
 */
@Mapper(componentModel = "spring")
public interface ResourceMapper {

    /**
     * Converts a Resource entity to a ResourceDTO.
     *
     * @param resource the Resource entity to convert
     * @return the converted ResourceDTO
     */
    @Mappings({
            @Mapping(source = "userId", target = "ownerId"),
            @Mapping(source = "id", target = "resourceId"),
            @Mapping(source = "name", target = "resourceName"),
            @Mapping(source = "type", target = "resourceType")
    })
    ResourceDTO toDTO(Resource resource);

    /**
     * Converts a Resource entity along with its associated User entity to a ResourceWithOwnerDTO.
     *
     * @param resource the Resource entity to convert
     * @param user the associated User entity
     * @return the converted ResourceWithOwnerDTO
     */
    @Mappings({
            @Mapping(source = "resource.id", target = "resourceId"),
            @Mapping(source = "resource.name", target = "resourceName"),
            @Mapping(source = "resource.type", target = "resourceType"),
            @Mapping(source = "user.id", target = "ownerId"),
            @Mapping(source = "user.username", target = "ownerName")
    })
    ResourceWithOwnerDTO toResourceWithOwnerDTO(Resource resource, User user);
}
