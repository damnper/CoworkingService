package ru.y_lab.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.y_lab.dto.RegisterRequestDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayName("RequestUtil Tests")
class RequestUtilTest {

    private HttpServletRequest req;

    @BeforeEach
    void setUp() {
        req = Mockito.mock(HttpServletRequest.class);
    }

    /**
     * Test for getting the request body as a String.
     * This test verifies that the getRequestBody method correctly reads the request body.
     */
    @Test
    @DisplayName("Get Request Body")
    void getRequestBody() throws IOException {
        String mockBody = "test body";
        BufferedReader reader = new BufferedReader(new StringReader(mockBody));
        when(req.getReader()).thenReturn(reader);

        String result = RequestUtil.getRequestBody(req);

        assertEquals(mockBody, result);
    }

    /**
     * Test for parsing a JSON request body to a DTO.
     * This test verifies that the parseRequest method correctly parses the JSON string into a DTO object.
     */
    @Test
    @DisplayName("Parse Request")
    void parseRequest() throws JsonProcessingException {
        String json = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        RegisterRequestDTO result = RequestUtil.parseRequest(json, RegisterRequestDTO.class);

        assertEquals("testuser", result.username());
        assertEquals("testpass", result.password());
    }

    /**
     * Test for handling invalid JSON during parsing.
     * This test verifies that the parseRequest method throws a JsonProcessingException when given invalid JSON.
     */
    @Test
    @DisplayName("Parse Request with Invalid JSON")
    void parseRequest_withInvalidJson() {
        String invalidJson = "invalid json";

        assertThrows(JsonProcessingException.class, () -> RequestUtil.parseRequest(invalidJson, RegisterRequestDTO.class));
    }
}
