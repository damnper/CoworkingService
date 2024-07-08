package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.service.UserService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class UserServletTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private UserServlet userServlet;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userServlet = new UserServlet(userService);  // Используем моки для UserService

        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    @Test
    void doPost_registerUser() throws Exception {
        when(req.getPathInfo()).thenReturn("/register");

        userServlet.doPost(req, resp);

        verify(userService, times(1)).registerUser(req, resp);
    }

    @Test
    void doPost_loginUser() throws Exception {
        when(req.getPathInfo()).thenReturn("/login");

        userServlet.doPost(req, resp);

        verify(userService, times(1)).loginUser(req, resp);
    }

    @Test
    void doPost_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        userServlet.doPost(req, resp);

        verify(resp, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid POST endpoint");
    }

    @Test
    void doGet_getUserById() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("userId")).thenReturn("1");

        userServlet.doGet(req, resp);

        verify(userService, times(1)).getUserById(req, resp);
    }

    @Test
    void doGet_getAllUsers() throws Exception {
        when(req.getPathInfo()).thenReturn("/all");

        userServlet.doGet(req, resp);

        verify(userService, times(1)).getAllUsers(req, resp);
    }

    @Test
    void doGet_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        userServlet.doGet(req, resp);

        verify(resp, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid GET endpoint or missing userId parameter");
    }

    @Test
    void doPut_updateUser() throws Exception {
        userServlet.doPut(req, resp);

        verify(userService, times(1)).updateUser(req, resp);
    }

    @Test
    void doDelete_deleteUser() throws Exception {
        userServlet.doDelete(req, resp);

        verify(userService, times(1)).deleteUser(req, resp);
    }

}
