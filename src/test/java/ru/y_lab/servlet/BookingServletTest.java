package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.y_lab.service.BookingService;
import ru.y_lab.util.ResponseUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BookingServlet class.
 */
@DisplayName("BookingServlet Tests")
class BookingServletTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private BookingServlet bookingServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bookingServlet = new BookingServlet(bookingService);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    /**
     * Test for adding a booking using POST /add endpoint.
     * This test verifies that the BookingService.addBooking method is called when the endpoint is "/add".
     */
    @Test
    @DisplayName("POST /add - Add Booking")
    void doPost_addBooking() throws Exception {
        when(req.getPathInfo()).thenReturn("/add");

        bookingServlet.doPost(req, resp);

        verify(bookingService, times(1)).addBooking(req, resp);
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
            bookingServlet.doPost(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid POST endpoint", messageCaptor.getValue());
        }
    }

    /**
     * Test for getting a booking by ID using GET / endpoint.
     * This test verifies that the BookingService.getBookingById method is called when the bookingId parameter is provided.
     */
    @Test
    @DisplayName("GET / - Get Booking by ID")
    void doGet_getBookingById() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("bookingId")).thenReturn("1");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getBookingById(req, resp);
    }

    /**
     * Test for getting user bookings using GET /getUserBookings endpoint.
     * This test verifies that the BookingService.getUserBookings method is called when the endpoint is "/getUserBookings".
     */
    @Test
    @DisplayName("GET /getUserBookings - Get User Bookings")
    void doGet_getUserBookings() throws Exception {
        when(req.getPathInfo()).thenReturn("/getUserBookings");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getUserBookings(req, resp);
    }

    /**
     * Test for getting all bookings using GET /getAllBookings endpoint (Admin).
     * This test verifies that the BookingService.getAllBookings method is called when the endpoint is "/getAllBookings".
     */
    @Test
    @DisplayName("GET /getAllBookings - Get All Bookings (Admin)")
    void doGet_getAllBookings() throws Exception {
        when(req.getPathInfo()).thenReturn("/getAllBookings");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getAllBookings(req, resp);
    }

    /**
     * Test for getting bookings by date using GET /getBookingByDate endpoint (Admin).
     * This test verifies that the BookingService.getBookingsByDate method is called when the endpoint is "/getBookingByDate".
     */
    @Test
    @DisplayName("GET /getBookingByDate - Get Booking By Date (Admin)")
    void doGet_getBookingByDate() throws Exception {
        when(req.getPathInfo()).thenReturn("/getBookingByDate");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getBookingsByDate(req, resp);
    }

    /**
     * Test for getting bookings by user ID using GET /getBookingsByUserId endpoint (Admin).
     * This test verifies that the BookingService.getBookingsByUserId method is called when the endpoint is "/getBookingsByUserId".
     */
    @Test
    @DisplayName("GET /getBookingsByUserId - Get Bookings By User ID (Admin)")
    void doGet_getBookingsByUserId() throws Exception {
        when(req.getPathInfo()).thenReturn("/getBookingsByUserId");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getBookingsByUserId(req, resp);
    }

    /**
     * Test for getting bookings by resource ID using GET /getBookingsByResourceId endpoint (Admin).
     * This test verifies that the BookingService.getBookingsByResourceId method is called when the endpoint is "/getBookingsByResourceId".
     */
    @Test
    @DisplayName("GET /getBookingsByResourceId - Get Bookings By Resource ID (Admin)")
    void doGet_getBookingsByResourceId() throws Exception {
        when(req.getPathInfo()).thenReturn("/getBookingsByResourceId");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getBookingsByResourceId(req, resp);
    }

    /**
     * Test for getting available slots using GET /getAvailableSlots endpoint.
     * This test verifies that the BookingService.getAvailableSlots method is called when the endpoint is "/getAvailableSlots".
     */
    @Test
    @DisplayName("GET /getAvailableSlots - Get Available Slots")
    void doGet_getAvailableSlots() throws Exception {
        when(req.getPathInfo()).thenReturn("/getAvailableSlots");

        bookingServlet.doGet(req, resp);

        verify(bookingService, times(1)).getAvailableSlots(req, resp);
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
            bookingServlet.doGet(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid GET endpoint or missing bookingId parameter", messageCaptor.getValue());
        }
    }

    /**
     * Test for updating a booking using PUT / endpoint.
     * This test verifies that the BookingService.updateBooking method is called when the bookingId parameter is provided.
     */
    @Test
    @DisplayName("PUT / - Update Booking")
    void doPut_updateBooking() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("bookingId")).thenReturn("1");

        bookingServlet.doPut(req, resp);

        verify(bookingService, times(1)).updateBooking(req, resp);
    }

    /**
     * Test for handling an invalid PUT endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("PUT /invalid - Invalid Endpoint")
    void doPut_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            bookingServlet.doPut(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid PUT endpoint or missing bookingId parameter", messageCaptor.getValue());
        }
    }

    /**
     * Test for deleting a booking using DELETE / endpoint.
     * This test verifies that the BookingService.deleteBooking method is called when the bookingId parameter is provided.
     */
    @Test
    @DisplayName("DELETE / - Delete Booking")
    void doDelete_deleteBooking() throws Exception {
        when(req.getPathInfo()).thenReturn(null);
        when(req.getParameter("bookingId")).thenReturn("1");

        bookingServlet.doDelete(req, resp);

        verify(bookingService, times(1)).deleteBooking(req, resp);
    }

    /**
     * Test for handling an invalid DELETE endpoint.
     * This test verifies that the ResponseUtil.sendErrorResponse method is called with the correct parameters when an invalid endpoint is accessed.
     */
    @Test
    @DisplayName("DELETE /invalid - Invalid Endpoint")
    void doDelete_invalidEndpoint() throws Exception {
        when(req.getPathInfo()).thenReturn("/invalid");

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = mockStatic(ResponseUtil.class)) {
            bookingServlet.doDelete(req, resp);

            ArgumentCaptor<HttpServletResponse> responseCaptor = ArgumentCaptor.forClass(HttpServletResponse.class);
            ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            responseUtilMockedStatic.verify(() -> ResponseUtil.sendErrorResponse(responseCaptor.capture(), statusCaptor.capture(), messageCaptor.capture()));

            assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());
            assertEquals("Invalid PUT endpoint or missing bookingId parameter", messageCaptor.getValue());
        }
    }
}
