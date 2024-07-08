package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.y_lab.service.UserService;
import ru.y_lab.util.ResponseUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServlet class.
 */
@DisplayName("UserServlet Tests")
class UserServletTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private UserServlet userServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userServlet = new UserServlet(userService);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }


    /**
     * Test for registering a user using POST /register endpoint.
     * This test verifies that the UserService.registerUser method is called when the endpoint is "/register".
     */
    @Test
    @DisplayName("POST /register - Register User")
    void doPost_registerUser() throws Exception {
        when(req.getPathInfo()).thenReturn("/register");

        userServlet.doPost(req, resp);

        verify(userService, times(1)).registerUser(req, resp);
    }

    /**
     * Test for logging in a user using POST /login endpoint.
     * This test verifies that the UserService.loginUser method is called when the endpoint is "/login".
     */
    @Test
    @DisplayName("POST /login - Login User")
    void doPost_loginUser() throws Exception {
        when(req.getPathInfo()).thenReturn("/login");

        userServlet.doPost(req, resp);

        verify(userService, times(1)).loginUser(req, resp);
    }

    /**
     * Test for handling an invalid POST endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("POST /invalid - Invalid Endpoint")
    void doPost_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            userServlet.doPost(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid POST endpoint", messageCaptor.getValue());
        }
    }

    /**
     * Test for getting a user by ID using GET / endpoint.
     * This test verifies that the UserService.getUserById method is called when the userId parameter is provided.
     */
    @Test
    @DisplayName("GET / - Get User by ID")
    void doGet_getUserById() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("userId")).thenReturn("1");

        userServlet.doGet(req, resp);

        verify(userService, times(1)).getUserById(req, resp);
    }

    /**
     * Test for getting all users using GET /all endpoint.
     * This test verifies that the UserService.getAllUsers method is called when the endpoint is "/all".
     */
    @Test
    @DisplayName("GET /all - Get All Users")
    void doGet_getAllUsers() throws Exception {
        when(req.getPathInfo()).thenReturn("/all");

        userServlet.doGet(req, resp);

        verify(userService, times(1)).getAllUsers(req, resp);
    }

    /**
     * Test for handling an invalid GET endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("GET /invalid - Invalid Endpoint")
    void doGet_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            userServlet.doGet(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid GET endpoint or missing userId parameter", messageCaptor.getValue());
        }
    }

    /**
     * Test for updating a user using PUT / endpoint.
     * This test verifies that the UserService.updateUser method is called correctly.
     */
    @Test
    @DisplayName("PUT / - Update User")
    void doPut_updateUser() throws Exception {
        userServlet.doPut(req, resp);

        verify(userService, times(1)).updateUser(req, resp);
    }

    /**
     * Test for deleting a user using DELETE / endpoint.
     * This test verifies that the UserService.deleteUser method is called correctly.
     */
    @Test
    @DisplayName("DELETE / - Delete User")
    void doDelete_deleteUser() throws Exception {
        userServlet.doDelete(req, resp);

        verify(userService, times(1)).deleteUser(req, resp);
    }
}
