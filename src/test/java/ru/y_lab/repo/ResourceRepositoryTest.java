package ru.y_lab.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.model.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceRepositoryTest {

    private ResourceRepository resourceRepository;

    @BeforeEach
    public void setUp() {
        resourceRepository = new ResourceRepository();
    }

    @Test
    public void testAddResource() throws ResourceNotFoundException {
        Resource resource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.addResource(resource);

        Resource retrievedResource = resourceRepository.getResourceById("1");
        assertNotNull(retrievedResource);
        assertEquals(resource, retrievedResource);
    }

    @Test
    public void testGetResourceById() throws ResourceNotFoundException {
        Resource resource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.addResource(resource);

        Resource retrievedResource = resourceRepository.getResourceById("1");
        assertNotNull(retrievedResource);
        assertEquals(resource, retrievedResource);
    }

    @Test
    public void testUpdateResource() throws ResourceNotFoundException {
        Resource resource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.addResource(resource);

        Resource updatedResource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.updateResource(updatedResource);

        Resource retrievedResource = resourceRepository.getResourceById("1");
        assertNotNull(retrievedResource);
        assertEquals(updatedResource, retrievedResource);
    }

    @Test
    public void testDeleteResource() throws ResourceNotFoundException {
        Resource resource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.addResource(resource);

        resourceRepository.deleteResource("1");
        assertThrows(ResourceNotFoundException.class, () -> resourceRepository.getResourceById("1"));
    }

    @Test
    public void testGetAllResources() throws ResourceNotFoundException {
        Resource resource1 = new Resource("1", "2", "Black room", "Workspace");
        Resource resource2 = new Resource("2", "3", "White room", "Workspace");
        resourceRepository.addResource(resource1);
        resourceRepository.addResource(resource2);

        List<Resource> allResources = resourceRepository.getAllResources();
        assertEquals(2, allResources.size());
        assertTrue(allResources.contains(resource1));
        assertTrue(allResources.contains(resource2));
    }

    @Test
    public void testAddDuplicateResource() throws ResourceNotFoundException {
        Resource resource = new Resource("1", "2", "Black room", "Workspace");
        resourceRepository.addResource(resource);

        assertThrows(ResourceNotFoundException.class, () -> resourceRepository.addResource(resource));
    }

    @Test
    public void testDeleteNonExistentResource() {
        String nonExistentResourceId = "nonexistent_id";

        assertThrows(ResourceNotFoundException.class, () -> resourceRepository.deleteResource(nonExistentResourceId));
    }
}
