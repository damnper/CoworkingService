package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.UserServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * UserServlet handles HTTP requests for user-related operations.
 */
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    /**
     * Handles HTTP POST requests for user-related operations like registering and logging in a user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if ("/register".equals(path)) {
            registerUser(req, resp);
        } else if ("/login".equals(path)) {
            loginUser(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid POST endpoint");
        }
    }

    /**
     * Handles HTTP GET requests for retrieving users.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        if ("/all".equals(path)) {
            getAllUsers(req, resp);
        } else {
            getUserById(req, resp);
        }
    }

    /**
     * Handles HTTP PUT requests for updating user information.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = authenticateAndAuthorize(req, resp, null);
        if (currentUser == null) {
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            UserDTO updatedUser = userService.updateUser(requestBody, currentUser);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(updatedUser.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error updating user account: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP DELETE requests for deleting a user account.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = authenticateAndAuthorize(req, resp, null);
        if (currentUser == null) {
            return;
        }

        try {
            userService.deleteUser(currentUser.getId());
            req.getSession().invalidate();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User account deleted successfully");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error deleting user account: " + e.getMessage());
        }
    }


    /**
     * Registers a new user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    private void registerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            UserDTO userDTO = userService.registerUser(requestBody);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(userDTO.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Logs in a user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    private void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            UserDTO userDTO = userService.loginUser(requestBody);

            HttpSession session = req.getSession();
            session.setAttribute("currentUser", userDTO);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(userDTO.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Retrieves all users.
     *
     * @param resp the HttpServletResponse object
     */
    @SneakyThrows
    private void getAllUsers(HttpServletRequest req, HttpServletResponse resp) {
        UserDTO currentUser = authenticateAndAuthorize(req, resp, "ADMIN");
        if (currentUser == null) {
            return;
        }

        try {
            List<UserDTO> users = userService.viewAllUsers();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(users.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @SneakyThrows
    private void getUserById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Long userId = Long.parseLong(req.getParameter("id"));
            UserDTO userDTO = userService.getUserById(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(userDTO.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Authenticates the user and checks their role if required.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @param requiredRole the required role for authorization (can be null if no role check is needed)
     * @return the authenticated UserDTO or null if authentication/authorization fails
     */
    private UserDTO authenticateAndAuthorize(HttpServletRequest req, HttpServletResponse resp, String requiredRole) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("User is not authenticated");
            return null;
        }

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");

        if (requiredRole != null && !requiredRole.equals(currentUser.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("Access denied");
            return null;
        }

        return currentUser;
    }
}
