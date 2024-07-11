package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.y_lab.service.ResourceService;
import ru.y_lab.util.ResponseUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ResourceServlet class.
 */
@DisplayName("ResourceServlet Tests")
public class ResourceServletTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private ResourceServlet resourceServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resourceServlet = new ResourceServlet(resourceService);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    /**
     * Test for adding a resource using POST /add endpoint.
     * This test verifies that the ResourceService.addResource method is called when the endpoint is "/add".
     */
    @Test
    @DisplayName("POST /add - Add Resource")
    void doPost_addResource() throws Exception {
        when(req.getPathInfo()).thenReturn("/add");

        resourceServlet.doPost(req, resp);

        verify(resourceService, times(1)).addResource(req, resp);
    }

    /**
     * Test for getting a resource by ID using GET / endpoint.
     * This test verifies that the ResourceService.getResourceById method is called when the resourceId parameter is provided.
     */
    @Test
    @DisplayName("GET / - Get Resource by ID")
    void doGet_getResourceById() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("resourceId")).thenReturn("1");

        resourceServlet.doGet(req, resp);

        verify(resourceService, times(1)).getResourceById(req, resp);
    }

    /**
     * Test for getting all resources using GET /all endpoint.
     * This test verifies that the ResourceService.getAllResources method is called when the endpoint is "/all".
     */
    @Test
    @DisplayName("GET /all - Get All Resources")
    void doGet_getAllResources() throws Exception {
        when(req.getPathInfo()).thenReturn("/all");

        resourceServlet.doGet(req, resp);

        verify(resourceService, times(1)).getAllResources(req, resp);
    }

    /**
     * Test for updating a resource using PUT / endpoint.
     * This test verifies that the ResourceService.updateResource method is called when the resourceId parameter is provided.
     */
    @Test
    @DisplayName("PUT / - Update Resource")
    void doPut_updateResource() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("resourceId")).thenReturn("1");

        resourceServlet.doPut(req, resp);

        verify(resourceService, times(1)).updateResource(req, resp);
    }

    /**
     * Test for deleting a resource using DELETE / endpoint.
     * This test verifies that the ResourceService.deleteResource method is called when the resourceId parameter is provided.
     */
    @Test
    @DisplayName("DELETE / - Delete Resource")
    void doDelete_deleteResource() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("resourceId")).thenReturn("1");

        resourceServlet.doDelete(req, resp);

        verify(resourceService, times(1)).deleteResource(req, resp);
    }

    /**
     * Test for handling an invalid POST endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("POST /invalid - Invalid Endpoint")
    void doPost_invalidEndpoint() {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            resourceServlet.doPost(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid POST endpoint", messageCaptor.getValue());
        }
    }

    /**
     * Test for handling an invalid GET endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("GET /invalid - Invalid Endpoint")
    void doGet_invalidEndpoint() {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            resourceServlet.doGet(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid GET endpoint or missing resourceId parameter", messageCaptor.getValue());
        }
    }

    /**
     * Test for handling an invalid PUT endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("PUT /invalid - Invalid Endpoint")
    void doPut_invalidEndpoint() {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            resourceServlet.doPut(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid PUT endpoint or missing resourceId parameter", messageCaptor.getValue());
        }
    }

    /**
     * Test for handling an invalid DELETE endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("DELETE /invalid - Invalid Endpoint")
    void doDelete_invalidEndpoint() {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            resourceServlet.doDelete(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid PUT endpoint or missing resourceId parameter", messageCaptor.getValue());
        }
    }

}
