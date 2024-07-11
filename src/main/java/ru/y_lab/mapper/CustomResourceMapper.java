package ru.y_lab.mapper;

import org.springframework.stereotype.Component;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;

@Component
public class CustomResourceMapper {

    public Resource toEntity(ResourceDTO resourceDTO) {
        if (resourceDTO == null) return null;

        return Resource.builder()
                .id(resourceDTO.id())
                .userId(resourceDTO.userId())
                .name(resourceDTO.name())
                .type(resourceDTO.type())
                .build();
    }

    public ResourceDTO toDTO(Resource resource) {
        if (resource == null) return null;

        return new ResourceDTO(
                resource.getId(),
                resource.getUserId(),
                resource.getName(),
                resource.getType()
        );
    }

    /**
     * Converts a Resource and User to a ResourceWithOwnerDTO.
     *
     * @param resource the Resource object
     * @param user the User object
     * @return the ResourceWithOwnerDTO containing combined information from Resource and User
     */
    public ResourceWithOwnerDTO toResourceWithOwnerDTO(Resource resource, User user) {
        if (resource == null || user == null) {
            return null;
        }

        return new ResourceWithOwnerDTO(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                user.getId(),
                user.getUsername()
        );
    }
}
