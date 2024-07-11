package ru.y_lab.util;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.UserDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("ResponseUtil Tests")
class ResponseUtilTest {

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    /**
     * Test for sending a success response with data.
     * This test verifies that the sendSuccessResponse method sends the correct status code and JSON response.
     */
    @Test
    @DisplayName("Send Success Response with Data")
    void sendSuccessResponse_withData() throws IOException {
        UserDTO userDTO = new UserDTO(1L, "testuser", "testpass", "USER");
        int statusCode = 200;

        ResponseUtil.sendSuccessResponse(resp, statusCode, userDTO);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(statusCode, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"username\":\"testuser\",\"password\":\"testpass\",\"role\":\"USER\"}", stringWriter.toString().trim());
    }

    /**
     * Test for sending a success response with a status code.
     * This test verifies that the sendSuccessResponse method sets the correct status code and content type.
     */
    @Test
    @DisplayName("Send Success Response with Status Code")
    void sendSuccessResponse_withStatusCode() {
        int statusCode = 204;

        ResponseUtil.sendSuccessResponse(resp, statusCode);

        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(statusCode, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
    }

    /**
     * Test for sending an error response with a message.
     * This test verifies that the sendErrorResponse method sends the correct status code and error message in JSON format.
     */
    @Test
    @DisplayName("Send Error Response")
    void sendErrorResponse() throws IOException {
        int statusCode = 400;
        String errorMessage = "Invalid request";

        ResponseUtil.sendErrorResponse(resp, statusCode, errorMessage);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(statusCode, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Invalid request\"}", stringWriter.toString());
    }

    /**
     * Test for creating a session for a user.
     * This test verifies that the createSession method correctly sets the currentUser attribute in the session.
     */
    @Test
    @DisplayName("Create Session")
    void createSession() {
        UserDTO userDTO = new UserDTO(1L, "testuser", "testpass", "USER");
        HttpSession session = mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        ResponseUtil.createSession(req, userDTO);

        verify(session).setAttribute("currentUser", userDTO);
    }
}
