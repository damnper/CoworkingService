package ru.y_lab.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility class for handle HTTP request.
 */
public class RequestUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads the request body from the HttpServletRequest.
     *
     * @param req the HttpServletRequest object
     * @return the request body as a String
     * @throws IOException if an input or output exception occurred
     */
    public static String getRequestBody(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /**
     * Parses the request from JSON string.
     *
     * @param requestBody the JSON string of the request
     * @param clazz the class of the DTO to parse
     * @return the parsed DTO object
     * @throws JsonProcessingException if processing the JSON input fails
     */
    public static <T> T parseRequest(String requestBody, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(requestBody, clazz);
    }
}
