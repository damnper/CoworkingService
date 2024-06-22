package ru.y_lab.repo;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ResourceRepository class provides methods to manage resources.
 * It stores resource data in a HashMap and supports CRUD operations.
 */
public class ResourceRepository {

    private final Map<String, Resource> resources = new HashMap<>();

    /**
     * Adds a new resource to the repository.
     * @param resource the resource to be added
     */
    public void addResource(Resource resource) throws ResourceNotFoundException {
        if (resources.containsKey(resource.getId())) {
            throw new ResourceNotFoundException("Resource with ID " + resource.getId() + " already exists");
        }
        resources.put(resource.getId(), resource);
    }

    /**
     * Retrieves a resource by its ID.
     * @param id the ID of the resource
     * @return the resource with the specified ID, or null if not found
     */
    public Resource getResourceById(String id) throws ResourceNotFoundException {
        Resource resource = resources.get(id);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource with ID " + id + " not found");
        }
        return resource;
    }

    /**
     * Updates an existing resource in the repository.
     * @param resource the resource with updated information
     */
    public void updateResource(Resource resource) throws ResourceNotFoundException {
        if (!resources.containsKey(resource.getId())) {
            throw new ResourceNotFoundException("Resource with ID " + resource.getId() + " not found");
        }
        resources.put(resource.getId(), resource);
    }

    /**
     * Deletes a resource from the repository by its ID.
     * @param id the ID of the resource to be deleted
     */
    public void deleteResource(String id) throws ResourceNotFoundException {
        if (resources.remove(id) == null) {
            throw new ResourceNotFoundException("Resource with ID " + id + " not found");
        }
    }

    /**
     * Retrieves all resources from the repository.
     * @return a list of all resources
     */
    public List<Resource> getAllResources() {
        return new ArrayList<>(resources.values());
    }
}
