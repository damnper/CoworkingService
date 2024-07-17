package ru.y_lab.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTests {

    @Test
    public void testResourceConstructorAndGetters() {
        Resource resource = new Resource(1L, 2L, "ResourceName", "ResourceType");

        assertEquals(1L, resource.getId());
        assertEquals(2L, resource.getUserId());
        assertEquals("ResourceName", resource.getName());
        assertEquals("ResourceType", resource.getType());
    }

    @Test
    public void testResourceSetters() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setUserId(2L);
        resource.setName("ResourceName");
        resource.setType("ResourceType");

        assertEquals(1L, resource.getId());
        assertEquals(2L, resource.getUserId());
        assertEquals("ResourceName", resource.getName());
        assertEquals("ResourceType", resource.getType());
    }

    @Test
    public void testResourceBuilder() {
        Resource resource = Resource.builder()
                .id(3L)
                .userId(4L)
                .name("BuilderResource")
                .type("BuilderType")
                .build();

        assertEquals(3L, resource.getId());
        assertEquals(4L, resource.getUserId());
        assertEquals("BuilderResource", resource.getName());
        assertEquals("BuilderType", resource.getType());
    }

    @Test
    public void testResourceEqualsAndHashCode() {
        Resource resource1 = new Resource(1L, 2L, "Name1", "Type1");
        Resource resource2 = new Resource(1L, 2L, "Name1", "Type1");
        Resource resource3 = new Resource(2L, 3L, "Name2", "Type2");

        assertEquals(resource1, resource2);
        assertNotEquals(resource1, resource3);
        assertNotEquals(resource2, resource3);

        assertEquals(resource1.hashCode(), resource2.hashCode());
        assertNotEquals(resource1.hashCode(), resource3.hashCode());
    }

    @Test
    public void testResourceToString() {
        Resource resource = new Resource(1L, 2L, "ResourceName", "ResourceType");
        String expectedString = "Resource(resourceId=1, ownerId=2, resourceName=ResourceName, resourceType=ResourceType)";
        assertEquals(expectedString, resource.toString());
    }

    @Test
    public void testResourceDefaultConstructor() {
        Resource resource = new Resource();
        assertNull(resource.getId());
        assertNull(resource.getUserId());
        assertNull(resource.getName());
        assertNull(resource.getType());
    }

    @Test
    public void testResourceEqualsWithDifferentFields() {
        Resource resource1 = new Resource(1L, 2L, "Name1", "Type1");
        Resource resource2 = new Resource(1L, 2L, "Name1", "Type2");

        assertNotEquals(resource1, resource2);
    }
}
