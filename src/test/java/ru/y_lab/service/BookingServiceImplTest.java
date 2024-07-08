package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.BookingDTO;
import ru.y_lab.dto.BookingWithOwnerResourceDTO;
import ru.y_lab.dto.UpdateBookingRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomBookingMapper;
import ru.y_lab.mapper.CustomDateTimeMapper;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.util.AuthenticationUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("BookingServiceImpl Tests - addBooking")
class BookingServiceImplTest {

    @Mock
    private AuthenticationUtil authUtil;

    @Mock
    private CustomBookingMapper bookingMapper;

    @Mock
    private CustomDateTimeMapper dateTimeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(authUtil, bookingMapper, dateTimeMapper, userRepository, resourceRepository, bookingRepository);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
    }

    /**
     * Test for successfully adding a booking.
     * This test verifies that a booking is successfully added and a success response is sent.
     */
    @Test
    @DisplayName("Add Booking")
    void addBooking() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1).plusHours(2);
        long epochMilli = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endMilli = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Booking booking = Booking.builder()
                .userId(currentUser.id())
                .resourceId(resource.getId())
                .startTime(startTime)
                .endTime(endTime)
                .build();
        BookingDTO bookingDTO = new BookingDTO(1L, 1L, 1L, startTime.toString(), endTime.toString());

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"resourceId\": 1, \"startTime\": " + epochMilli + ", \"endTime\": " + endMilli + "}")));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(dateTimeMapper.toLocalDateTime(anyLong())).thenAnswer(invocation -> LocalDateTime.ofEpochSecond(invocation.getArgument(0), 0, ZoneOffset.UTC));
        when(bookingRepository.getBookingsByResourceId(1L)).thenReturn(Optional.of(List.of()));
        when(bookingRepository.saveBooking(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(bookingDTO);

        bookingService.addBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(201, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"userId\":1,\"resourceId\":1,\"startTime\":\"" + startTime + "\",\"endTime\":\"" + endTime + "\"}", stringWriter.toString().trim());
    }

    /**
    * Test for unauthorized access.
    * This test verifies that an error response is sent when the user is not authorized.
    */
    @Test
    @DisplayName("Unauthorized Access")
    void addBooking_unauthorized() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Unauthorized"));

        bookingService.addBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling resource not found.
     * This test verifies that an error response is sent when the resource is not found.
     */
    @Test
    @DisplayName("Resource Not Found Add Booking")
    void addBooking_resourceNotFound() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1).plusHours(2);
        long startEpoch = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endEpoch = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"resourceId\": 1, \"startTime\": " + startEpoch + ", \"endTime\": " + endEpoch + "}")));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.addBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by resource name: 1\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception")
    void addBooking_generalException() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1).plusHours(2);
        long startEpoch = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endEpoch = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"resourceId\": 1, \"startTime\": " + startEpoch + ", \"endTime\": " + endEpoch + "}")));
        when(resourceRepository.getResourceById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        bookingService.addBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - Success")
    void getBookingById_success() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        User user = User.builder().id(1L).username("user").password("password").build();
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString(),
                booking.getEndTime().toLocalTime().toString()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
        when(resourceRepository.getResourceById(booking.getResourceId())).thenReturn(Optional.of(resource));
        when(userRepository.getUserById(booking.getUserId())).thenReturn(Optional.of(user));
        when(bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user)).thenReturn(bookingWithOwnerResourceDTO);

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"userId\":1,\"resourceId\":1,\"bookingId\":1,\"username\":\"user\",\"resourceName\":\"Resource1\",\"resourceType\":\"Type1\",\"date\":\"" + booking.getStartTime().toLocalDate().toString() + "\",\"startTime\":\"" + booking.getStartTime().toLocalTime().toString() + "\",\"endTime\":\"" + booking.getEndTime().toLocalTime().toString() + "\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - Booking Not Found")
    void getBookingById_bookingNotFound() throws IOException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.empty());

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Booking not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - Resource Not Found")
    void getBookingById_resourceNotFound() throws IOException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - User Not Found")
    void getBookingById_userNotFound() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
        when(resourceRepository.getResourceById(booking.getResourceId())).thenReturn(Optional.of(resource));
        when(userRepository.getUserById(booking.getUserId())).thenReturn(Optional.empty());

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - Unauthorized")
    void getBookingById_unauthorized() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Unauthorized"));

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Booking By ID - General Exception")
    void getBookingById_generalException() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new RuntimeException("Internal server error"));

        bookingService.getBookingById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - Success")
    void getUserBookings_success() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        User user = User.builder().id(1L).username("user").password("password").build();
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString(),
                booking.getEndTime().toLocalTime().toString()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(bookingMapper.toBookingWithOwnerResourceDTO(any(Booking.class), any(Resource.class), any(User.class))).thenReturn(bookingWithOwnerResourceDTO);

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"userId\":1,\"resourceId\":1,\"bookingId\":1,\"username\":\"user\",\"resourceName\":\"Resource1\",\"resourceType\":\"Type1\",\"date\":\"" + booking.getStartTime().toLocalDate().toString() + "\",\"startTime\":\"" + booking.getStartTime().toLocalTime().toString() + "\",\"endTime\":\"" + booking.getEndTime().toLocalTime().toString() + "\"}]", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - No Bookings Found")
    void getUserBookings_noBookingsFound() throws IOException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(1L)).thenReturn(Optional.empty());

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User 1 have not any booking\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - Resource Not Found")
    void getUserBookings_resourceNotFound() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(User.builder().id(1L).username("user").password("password").build()));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - User Not Found")
    void getUserBookings_userNotFound() throws IOException, UserNotFoundException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - Unauthorized")
    void getUserBookings_unauthorized() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Unauthorized"));

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get User Bookings - General Exception")
    void getUserBookings_generalException() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new RuntimeException("Internal server error"));

        bookingService.getUserBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - Success")
    void getAllBookings_success() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        User user = User.builder().id(1L).username("user").password("password").build();
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString(),
                booking.getEndTime().toLocalTime().toString()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(bookingRepository.getAllBookings()).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(bookingMapper.toBookingWithOwnerResourceDTO(any(Booking.class), any(Resource.class), any(User.class))).thenReturn(bookingWithOwnerResourceDTO);

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"userId\":1,\"resourceId\":1,\"bookingId\":1,\"username\":\"user\",\"resourceName\":\"Resource1\",\"resourceType\":\"Type1\",\"date\":\"" + booking.getStartTime().toLocalDate().toString() + "\",\"startTime\":\"" + booking.getStartTime().toLocalTime().toString() + "\",\"endTime\":\"" + booking.getEndTime().toLocalTime().toString() + "\"}]", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - No Bookings Found")
    void getAllBookings_noBookingsFound() throws IOException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(bookingRepository.getAllBookings()).thenReturn(Optional.empty());

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"There are not any existing booking\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - Resource Not Found")
    void getAllBookings_resourceNotFound() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(bookingRepository.getAllBookings()).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(User.builder().id(1L).username("user").password("password").build()));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - User Not Found")
    void getAllBookings_userNotFound() throws IOException, UserNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(1L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(bookingRepository.getAllBookings()).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - Unauthorized")
    void getAllBookings_unauthorized() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new SecurityException("Unauthorized"));

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get All Bookings - General Exception")
    void getAllBookings_generalException() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new RuntimeException("Internal server error"));

        bookingService.getAllBookings(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }


    @Test
    @DisplayName("Get Bookings by User ID - Success")
    void getBookingsByUserId_success() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        User user = User.builder().id(2L).username("user").password("password").build();
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString(),
                booking.getEndTime().toLocalTime().toString()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(bookingRepository.getBookingsByUserId(2L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(bookingMapper.toBookingWithOwnerResourceDTO(any(Booking.class), any(Resource.class), any(User.class))).thenReturn(bookingWithOwnerResourceDTO);

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"userId\":2,\"resourceId\":1,\"bookingId\":1,\"username\":\"user\",\"resourceName\":\"Resource1\",\"resourceType\":\"Type1\",\"date\":\"" + booking.getStartTime().toLocalDate().toString() + "\",\"startTime\":\"" + booking.getStartTime().toLocalTime().toString() + "\",\"endTime\":\"" + booking.getEndTime().toLocalTime().toString() + "\"}]", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by User ID - No Bookings Found")
    void getBookingsByUserId_noBookingsFound() throws IOException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(bookingRepository.getBookingsByUserId(2L)).thenReturn(Optional.empty());

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"There are not any existing booking by user ID: 2\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by User ID - Resource Not Found")
    void getBookingsByUserId_resourceNotFound() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(bookingRepository.getBookingsByUserId(2L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(User.builder().id(2L).username("user").password("password").build()));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by User ID - User Not Found")
    void getBookingsByUserId_userNotFound() throws IOException, UserNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(bookingRepository.getBookingsByUserId(2L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.empty());

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 2\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by User ID - Unauthorized")
    void getBookingsByUserId_unauthorized() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new SecurityException("Unauthorized"));

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by User ID - General Exception")
    void getBookingsByUserId_generalException() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new RuntimeException("Internal server error"));

        bookingService.getBookingsByUserId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - Success")
    void getBookingsByResourceId_success() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        User user = User.builder().id(2L).username("user").password("password").build();
        Resource resource = Resource.builder().id(1L).name("Resource1").type("Type1").build();
        Booking booking = Booking.builder()
                .id(1L)
                .userId(user.getId())
                .resourceId(resource.getId())
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString(),
                booking.getEndTime().toLocalTime().toString()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(bookingRepository.getBookingsByResourceId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(bookingMapper.toBookingWithOwnerResourceDTO(any(Booking.class), any(Resource.class), any(User.class))).thenReturn(bookingWithOwnerResourceDTO);

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"userId\":2,\"resourceId\":1,\"bookingId\":1,\"username\":\"user\",\"resourceName\":\"Resource1\",\"resourceType\":\"Type1\",\"date\":\"" + booking.getStartTime().toLocalDate().toString() + "\",\"startTime\":\"" + booking.getStartTime().toLocalTime().toString() + "\",\"endTime\":\"" + booking.getEndTime().toLocalTime().toString() + "\"}]", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - No Bookings Found")
    void getBookingsByResourceId_noBookingsFound() throws IOException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(bookingRepository.getBookingsByResourceId(1L)).thenReturn(Optional.empty());

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"There are not any existing booking by user ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - Resource Not Found")
    void getBookingsByResourceId_resourceNotFound() throws IOException, UserNotFoundException, ResourceNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(bookingRepository.getBookingsByResourceId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(User.builder().id(2L).username("user").password("password").build()));
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - User Not Found")
    void getBookingsByResourceId_userNotFound() throws IOException, UserNotFoundException, BookingNotFoundException {
        UserDTO adminUser = new UserDTO(1L, "admin", "password", "ADMIN");
        Booking booking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(adminUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(bookingRepository.getBookingsByResourceId(1L)).thenReturn(Optional.of(List.of(booking)));
        when(userRepository.getUserById(2L)).thenReturn(Optional.empty());

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 2\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - Unauthorized")
    void getBookingsByResourceId_unauthorized() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new SecurityException("Unauthorized"));

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unauthorized\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Bookings by Resource ID - General Exception")
    void getBookingsByResourceId_generalException() throws IOException, BookingNotFoundException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new RuntimeException("Internal server error"));

        bookingService.getBookingsByResourceId(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Available Slots - Success")
    void getAvailableSlots_success() throws IOException, BookingNotFoundException {
        UserDTO userDTO = new UserDTO(1L, "user", "password", "USER");
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime dateTime = date.atStartOfDay();
        long dateMilli = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long resourceId = 1L;

        Booking booking1 = Booking.builder()
                .id(1L)
                .userId(userDTO.id())
                .resourceId(resourceId)
                .startTime(date.atTime(10, 0))
                .endTime(date.atTime(11, 0))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .userId(userDTO.id())
                .resourceId(resourceId)
                .startTime(date.atTime(14, 0))
                .endTime(date.atTime(15, 0))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(userDTO);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"resourceId\": 1, \"date\": \"" + dateMilli + "\"}")));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Optional.of(List.of(booking1, booking2)));
        when(dateTimeMapper.toLocalDate(anyLong())).thenReturn(date);

        bookingService.getAvailableSlots(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_OK, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"slotNumber\":1,\"slotStart\":\"09:00:00\",\"slotEnd\":\"10:00:00\"}," +
                "{\"slotNumber\":2,\"slotStart\":\"11:00:00\",\"slotEnd\":\"14:00:00\"}," +
                "{\"slotNumber\":3,\"slotStart\":\"15:00:00\",\"slotEnd\":\"18:00:00\"}]", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Get Available Slots - No Bookings Found")
    void getAvailableSlots_noBookingsFound() throws IOException, BookingNotFoundException {
        UserDTO userDTO = new UserDTO(1L, "user", "password", "USER");
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime dateTime = date.atStartOfDay();
        long dateMilli = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long resourceId = 1L;

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(userDTO);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"resourceId\": 1, \"date\": \"" + dateMilli + "\"}")));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Optional.empty());
        when(dateTimeMapper.toLocalDate(anyLong())).thenReturn(date);

        bookingService.getAvailableSlots(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Booking not found by resource ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Update Booking - Success")
    void updateBooking_success() throws IOException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        LocalDateTime originalStartTime = LocalDateTime.now().plusDays(1).plusHours(1);
        LocalDateTime originalEndTime = LocalDateTime.now().plusDays(1).plusHours(2);
        Booking existingBooking = Booking.builder()
                .id(1L)
                .userId(currentUser.id())
                .resourceId(1L)
                .startTime(originalStartTime)
                .endTime(originalEndTime)
                .build();

        LocalDateTime newStartTime = originalStartTime.plusHours(1);
        LocalDateTime newEndTime = originalEndTime.plusHours(1);
        long newStartEpoch = newStartTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long newEndEpoch = newEndTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.getBookingsByResourceId(existingBooking.getResourceId())).thenReturn(Optional.of(List.of(existingBooking)));
        when(authUtil.isUserAuthorizedToAction(currentUser, existingBooking.getUserId())).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"startTime\": " + newStartEpoch + ", \"endTime\": " + newEndEpoch + "}")));
        when(dateTimeMapper.toLocalDateTime(anyLong())).thenAnswer(invocation -> LocalDateTime.ofEpochSecond(invocation.getArgument(0), 0, ZoneOffset.UTC));
        when(bookingRepository.updateBooking(any(Booking.class))).thenReturn(existingBooking);
        when(bookingMapper.toDTO(any(Booking.class))).thenReturn(new BookingDTO(existingBooking.getId(), existingBooking.getUserId(), existingBooking.getResourceId(), newStartTime.toString(), newEndTime.toString()));

        bookingService.updateBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"userId\":1,\"resourceId\":1,\"startTime\":\"" + newStartTime + "\",\"endTime\":\"" + newEndTime + "\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Update Booking - Booking Not Found")
    void updateBooking_bookingNotFound() throws IOException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");

        Booking existingBooking = Booking.builder()
                .id(1L)
                .userId(currentUser.id())
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        UpdateBookingRequestDTO updateRequest = new UpdateBookingRequestDTO(
                existingBooking.getStartTime().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                existingBooking.getEndTime().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"startTime\": " + updateRequest.startTime() + ", \"endTime\": " + updateRequest.endTime() + "}")));
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.empty());

        bookingService.updateBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Booking not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Update Booking - Unauthorized")
    void updateBooking_unauthorized() throws IOException, BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking existingBooking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        UpdateBookingRequestDTO updateRequest = new UpdateBookingRequestDTO(
                existingBooking.getStartTime().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                existingBooking.getEndTime().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli()
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"startTime\": " + updateRequest.startTime() + ", \"endTime\": " + updateRequest.endTime() + "}")));
        when(authUtil.isUserAuthorizedToAction(currentUser, existingBooking.getUserId())).thenReturn(false);

        bookingService.updateBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Delete Booking - Success")
    void deleteBooking_success() throws BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking existingBooking = Booking.builder()
                .id(1L)
                .userId(currentUser.id())
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
        when(authUtil.isUserAuthorizedToAction(currentUser, existingBooking.getUserId())).thenReturn(true);

        bookingService.deleteBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(204, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertTrue(stringWriter.toString().trim().isEmpty());
    }

    @Test
    @DisplayName("Delete Booking - Booking Not Found")
    void deleteBooking_bookingNotFound() throws BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.empty());

        bookingService.deleteBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Booking not found by ID: 1\"}", stringWriter.toString().trim());
    }

    @Test
    @DisplayName("Delete Booking - Unauthorized")
    void deleteBooking_unauthorized() throws BookingNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "user", "password", "USER");
        Booking existingBooking = Booking.builder()
                .id(1L)
                .userId(2L)
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("bookingId")).thenReturn("1");
        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
        when(authUtil.isUserAuthorizedToAction(currentUser, existingBooking.getUserId())).thenReturn(false);

        bookingService.deleteBooking(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

}
