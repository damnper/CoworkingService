package ru.y_lab.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.y_lab.dto.ErrorResponse;
import ru.y_lab.dto.UserDTO;

import java.io.IOException;

/**
 * Utility class for sending HTTP responses.
 */
public class ResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a successful response with the data.
     *
     * @param resp the HttpServletResponse object
     * @param data the data to be sent in the response
     * @throws IOException if an input or output exception occurred
     */
    public static void sendSuccessResponse(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(data));
    }

    /**
     * Sends a success response with the given status code.
     * <p>
     * This method sets the HTTP status code and content type to "application/json" for a successful response.
     *
     * @param resp the HttpServletResponse object
     * @param statusCode the HTTP status code to set
     */
    public static void sendSuccessResponse(HttpServletResponse resp, int statusCode) {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
    }

    /**
     * Sends an error response with the given status code and message.
     * <p>
     * This method sets the HTTP status code and content type to "application/json",
     * and writes an error message in JSON format to the response body.
     *
     * @param resp the HttpServletResponse object
     * @param statusCode the HTTP status code to set
     * @param message the error message to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    public static void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(message)));
    }

    /**
     * Creates a session for the authenticated user.
     *
     * @param req the HttpServletRequest object
     * @param userDTO the authenticated UserDTO object
     */
    public static void createSession(HttpServletRequest req, UserDTO userDTO) {
        HttpSession session = req.getSession();
        session.setAttribute("currentUser", userDTO);
    }
}
