//package ru.y_lab.repo;
//
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import ru.y_lab.config.DatabaseConfig;
//import ru.y_lab.config.DatabaseManager;
//import ru.y_lab.exception.ResourceNotFoundException;
//import ru.y_lab.model.Resource;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Unit tests for the ResourceRepository class.
// */
//@Testcontainers
//@DisplayName("Unit Tests for ResourceRepositoryTest")
//public class ResourceRepositoryTest {
//
//    private ResourceRepository resourceRepository;
//
//    @Container
//    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    @BeforeAll
//    public static void setUpBeforeAll() {
//        postgresContainer.start();
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setDriverClassName("org.postgresql.Driver");
//        dbConfig.setUrl(postgresContainer.getJdbcUrl());
//        dbConfig.setUsername(postgresContainer.getUsername());
//        dbConfig.setPassword(postgresContainer.getPassword());
//        DatabaseManager.setConfig(dbConfig);
//
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("CREATE SCHEMA IF NOT EXISTS coworking_service");
//
//                stmt.execute("SET search_path TO coworking_service");
//
//                stmt.execute("CREATE TABLE coworking_service.resources (" +
//                        "id SERIAL PRIMARY KEY, " +
//                        "user_id BIGINT, " +
//                        "resourceName VARCHAR(255), " +
//                        "type VARCHAR(255))");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error setting up test database", e);
//        }
//    }
//
//    @AfterAll
//    public static void tearDownAfterAll() {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("DROP SCHEMA IF EXISTS coworking_service CASCADE");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error tearing down test database", e);
//        }
//    }
//
//    @BeforeEach
//    public void setUp() {
//        resourceRepository = new ResourceRepository();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("TRUNCATE TABLE coworking_service.resources RESTART IDENTITY CASCADE");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error tearing down the database after test", e);
//        }
//    }
//
//    /**
//    * Test case for adding a resource to the repository.
//     */
//    @Test
//    @DisplayName("Test Adding a New Resource")
//    public void testAddResource() throws ResourceNotFoundException {
//        Resource resource = new Resource(null, 1L, "Black room", "Workspace");
//        resourceRepository.addResource(resource);
//
//        Resource retrievedResource = resourceRepository.getResourceById(resource.getId()).orElse(null);
//        assertNotNull(retrievedResource);
//        assertEquals(resource.getUserId(), retrievedResource.getUserId());
//        assertEquals(resource.getName(), retrievedResource.getName());
//        assertEquals(resource.getType(), retrievedResource.getType());
//
//    }
//
//    /**
//     * Test case for retrieving a resource by ID.
//     */
//    @Test
//    @DisplayName("Test Retrieving a Resource by ID")
//    public void testGetResourceById() throws ResourceNotFoundException {
//        Resource resource = new Resource(null, 1L, "Black room", "Workspace");
//        resourceRepository.addResource(resource);
//
//        Resource retrievedResource = resourceRepository.getResourceById(resource.getId()).orElse(null);
//        assertNotNull(retrievedResource);
//        assertEquals(resource.getUserId(), retrievedResource.getUserId());
//        assertEquals(resource.getName(), retrievedResource.getName());
//        assertEquals(resource.getType(), retrievedResource.getType());
//    }
//
//    /**
//     * Test case for updating an existing resource.
//     *
//     * @throws ResourceNotFoundException if the resource to update is not found
//     */
//    @Test
//    @DisplayName("Test Updating an Existing Resource")
//    public void testUpdateResource() throws ResourceNotFoundException {
//        Resource resource = new Resource(null, 1L, "Black room", "Workspace");
//        resourceRepository.addResource(resource);
//
//        Resource updatedResource = new Resource(resource.getId(), 1L, "Black room", "Conference Room");
//        resourceRepository.updateResource(updatedResource);
//
//        Resource retrievedResource = resourceRepository.getResourceById(updatedResource.getId()).orElse(null);
//        assertNotNull(retrievedResource);
//        assertEquals(updatedResource.getType(), retrievedResource.getType());
//    }
//
//    /**
//     * Test case for deleting an existing resource.
//     *
//     * @throws ResourceNotFoundException if the resource to delete is not found
//     */
//    @Test
//    @DisplayName("Test Deleting an Existing Resource")
//    public void testDeleteResource() throws ResourceNotFoundException {
//        Resource resource = new Resource(null, 1L, "Black room", "Workspace");
//        Resource savedResource = resourceRepository.addResource(resource);
//
//        resourceRepository.deleteResource(savedResource.getId());
//
//        Optional<Resource> optionalResource = resourceRepository.getResourceById(savedResource.getId());
//        assertTrue(optionalResource.isEmpty(), "Resource should have been deleted");
//
//    }
//
//    /**
//     * Test case for retrieving all resources from the repository.
//     */
//    @Test
//    @DisplayName("Test Retrieving All Resources")
//    public void testGetAllResources() {
//        Resource resource1 = new Resource(null, 1L, "Black room", "Workspace");
//        Resource resource2 = new Resource(null, 2L, "White room", "Workspace");
//        resourceRepository.addResource(resource1);
//        resourceRepository.addResource(resource2);
//
//        List<Resource> allResources = resourceRepository.getAllResources();
//        assertEquals(2, allResources.size());
//        assertTrue(allResources.contains(resource1));
//        assertTrue(allResources.contains(resource2));
//    }
//}
