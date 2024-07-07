package ru.y_lab.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.impl.BookingServiceImpl;

import java.io.IOException;
import java.util.Objects;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static ru.y_lab.util.ResponseUtil.sendErrorResponse;

/**
 * BookingServlet handles HTTP requests for booking-related operations.
 */
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingServiceImpl();

    /**
     * Handles HTTP POST requests for adding a new booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path.equals("/add")) {
            bookingService.addBooking(req, resp);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid POST endpoint");
        }
    }

    /**
     * Handles HTTP GET requests for retrieving bookings.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String bookingId = req.getParameter("bookingId");

        if ((path == null || path.equals("/")) && bookingId != null) {
            bookingService.getBookingById(req, resp);
            return;
        }

        switch (Objects.requireNonNull(path)) {
            case "/getUserBookings" -> bookingService.getUserBookings(req, resp);
            case "/getAllBookings" -> bookingService.getAllBookings(req, resp); // admin
            case "/getBookingByDate" -> bookingService.getBookingsByDate(req, resp); // admin
            case "/getBookingsByUserId" -> bookingService.getBookingsByUserId(req, resp); // admin
            case "/getBookingsByResourceId" -> bookingService.getBookingsByResourceId(req, resp); // admin
            case "/getAvailableSlots" -> bookingService.getAvailableSlots(req, resp);
            default -> sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid GET endpoint or missing bookingId parameter");
        }
    }

    /**
     * Handles HTTP PATCH requests for updating an existing booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String bookingId = req.getParameter("bookingId");

        if ((path == null || path.equals("/")) && bookingId != null) {
            bookingService.updateBooking(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid PUT endpoint or missing bookingId parameter");
        }
    }

    /**
     * Handles HTTP DELETE requests for deleting an existing booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String bookingId = req.getParameter("bookingId");

        if ((path == null || path.equals("/")) && bookingId != null) {
            bookingService.deleteBooking(req, resp);
        } else {
            sendErrorResponse(resp, SC_BAD_REQUEST, "Invalid PUT endpoint or missing bookingId parameter");
        }
    }
}
