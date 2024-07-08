package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.UserServiceImpl;

import java.io.IOException;

import static ru.y_lab.util.ResponseUtil.sendErrorResponse;

/**
 * UserServlet handles HTTP requests for user-related operations.
 */
@Loggable
public class UserServlet extends HttpServlet {

    private final UserService userService;

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    public UserServlet() {
        userService = new UserServiceImpl();
    }

    /**
     * Handles HTTP POST requests for user-related operations like registering and logging in a user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        switch (path) {
            case "/register" -> userService.registerUser(req, resp);
            case "/login" -> userService.loginUser(req, resp);
            default -> sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid POST endpoint");
        }
    }

    /**
     * Handles HTTP GET requests for retrieving users.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String userIdParam = req.getParameter("userId");

        if ((path == null || path.equals("/")) && userIdParam != null) {
            userService.getUserById(req, resp);
        } else if ("/all".equals(path)) {
            userService.getAllUsers(req, resp);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid GET endpoint or missing userId parameter");
        }
    }

    /**
     * Handles HTTP PUT requests for updating user information.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        userService.updateUser(req, resp);
    }

    /**
     * Handles HTTP DELETE requests for deleting a user account.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        userService.deleteUser(req, resp);
    }

}
