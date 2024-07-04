package ru.y_lab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.y_lab.dto.BookingDTO;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.impl.BookingServiceImpl;

import java.util.List;

/**
 * BookingServlet handles HTTP requests for booking-related operations.
 */
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles HTTP POST requests for adding a new booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        try {
            if ("/add".equals(path)) {
                BookingDTO bookingDTO = bookingService.addBooking(req.getReader().readLine());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(bookingDTO));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid POST endpoint");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Handles HTTP PATCH requests for updating an existing booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            bookingService.updateBooking(req.getReader().readLine());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (BookingNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Handles HTTP GET requests for retrieving bookings.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        try {
            if ("/all".equals(path)) {
                List<BookingDTO> bookings = bookingService.viewUserBookings();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(bookings));
            } else if ("/available".equals(path)) {
                Long resourceId = Long.parseLong(req.getParameter("resourceId"));
                String dateStr = req.getParameter("date");
                List<BookingDTO> availableSlots = bookingService.viewAvailableSlots(resourceId, dateStr);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(availableSlots));
            } else {
                Long bookingId = Long.parseLong(req.getParameter("id"));
                BookingDTO bookingDTO = bookingService.getBookingById(bookingId);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(bookingDTO));
            }
        } catch (BookingNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    /**
     * Handles HTTP DELETE requests for deleting an existing booking.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    @SneakyThrows
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Long bookingId = Long.parseLong(req.getParameter("id"));
            bookingService.deleteBooking(bookingId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (BookingNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}
