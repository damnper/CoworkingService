package ru.y_lab.mapper;

import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.model.Resource;

public class CustomResourceMapper {

    // Преобразование из ResourceDTO в Resource
    public Resource toEntity(ResourceDTO resourceDTO) {
        if (resourceDTO == null) {
            return null;
        }

        return Resource.builder()
                .id(resourceDTO.getId())
                .userId(resourceDTO.getUserId())
                .name(resourceDTO.getName())
                .type(resourceDTO.getType())
                .build();
    }

    // Преобразование из Resource в ResourceDTO
    public ResourceDTO toDTO(Resource resource) {
        if (resource == null) {
            return null;
        }

        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(resource.getId());
        resourceDTO.setUserId(resource.getUserId());
        resourceDTO.setName(resource.getName());
        resourceDTO.setType(resource.getType());

        return resourceDTO;
    }
}
