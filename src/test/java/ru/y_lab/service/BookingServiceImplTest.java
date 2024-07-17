//package ru.y_lab.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.y_lab.dto.*;
//import ru.y_lab.exception.*;
//import ru.y_lab.mapper.BookingMapper;
//import ru.y_lab.mapper.CustomDateTimeMapper;
//import ru.y_lab.model.Booking;
//import ru.y_lab.model.Resource;
//import ru.y_lab.model.User;
//import ru.y_lab.service.impl.BookingServiceImpl;
//import ru.y_lab.util.AuthenticationUtil;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@DisplayName("BookingServiceImpl Tests - addBooking")
//class BookingServiceImplTest {
//
//    @Mock
//    private AuthenticationUtil authUtil;
//
//    @Mock
//    private BookingMapper bookingMapper;
//
//    @Mock
//    private CustomDateTimeMapper dateTimeMapper;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ResourceRepository resourceRepository;
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private HttpServletRequest req;
//
//    @InjectMocks
//    private BookingServiceImpl bookingService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        bookingService = new BookingServiceImpl(authUtil, bookingMapper, dateTimeMapper, userRepository, resourceRepository, bookingRepository);
//    }
//
//    @Test
//    @DisplayName("Add Booking")
//    void addBooking() {
//        AddBookingRequestDTO requestDTO = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 20, 10, 0);
//        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 20, 12, 0);
//        Booking savedBooking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(startDateTime).endTime(endDateTime).build();
//        BookingDTO bookingDTO = new BookingDTO(1L, 1L, 1L, "2023-07-12T10:00", "2023-07-12T12:00");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(dateTimeMapper.toLocalDateTime(1721469600000L)).thenReturn(startDateTime);
//        when(dateTimeMapper.toLocalDateTime(1721473200000L)).thenReturn(endDateTime);
//        when(bookingRepository.addBooking(any(Booking.class))).thenReturn(savedBooking);
//        when(bookingMapper.toDTO(savedBooking)).thenReturn(bookingDTO);
//
//        BookingDTO result = bookingService.addBooking(requestDTO, req);
//
//        assertEquals(bookingDTO, result);
//        verify(authUtil).authenticate(req, "USER");
//        verify(resourceRepository).getResourceById(1L);
//        verify(dateTimeMapper).toLocalDateTime(1721469600000L);
//        verify(dateTimeMapper).toLocalDateTime(1721473200000L);
//        verify(bookingRepository).addBooking(any(Booking.class));
//        verify(bookingMapper).toDTO(savedBooking);
//    }
//
//    @Test
//    @DisplayName("Add Booking - Resource Not Found")
//    void addBookingResourceNotFound() {
//        AddBookingRequestDTO requestDTO = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> bookingService.addBooking(requestDTO, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(resourceRepository).getResourceById(1L);
//        verifyNoInteractions(dateTimeMapper, bookingRepository, bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Add Booking - Authentication Failed")
//    void addBookingAuthenticationFailed() {
//        AddBookingRequestDTO requestDTO = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);
//
//        when(authUtil.authenticate(req, "USER")).thenThrow(new AuthenticateException("Authentication failed"));
//
//        assertThrows(AuthenticateException.class, () -> bookingService.addBooking(requestDTO, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verifyNoInteractions(resourceRepository, dateTimeMapper, bookingRepository, bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Add Booking - Booking Conflict")
//    void addBookingConflict() {
//        AddBookingRequestDTO requestDTO = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 20, 10, 0);
//        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 20, 12, 0);
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(dateTimeMapper.toLocalDateTime(1721469600000L)).thenReturn(startDateTime);
//        when(dateTimeMapper.toLocalDateTime(1721473200000L)).thenReturn(endDateTime);
//        doThrow(new BookingConflictException("Booking conflict")).when(bookingRepository).addBooking(any(Booking.class));
//
//        assertThrows(BookingConflictException.class, () -> bookingService.addBooking(requestDTO, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(resourceRepository).getResourceById(1L);
//        verify(dateTimeMapper).toLocalDateTime(1721469600000L);
//        verify(dateTimeMapper).toLocalDateTime(1721473200000L);
//        verify(bookingRepository).addBooking(any(Booking.class));
//        verifyNoInteractions(bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Get Booking By ID")
//    void getBookingById() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//        User user = User.builder().resourceId(1L).username("user").password("password").role("USER").build();
//        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource resourceName", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
//        when(bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user)).thenReturn(bookingWithOwnerResourceDTO);
//
//        BookingWithOwnerResourceDTO result = bookingService.getBookingById(1L, req);
//
//        assertEquals(bookingWithOwnerResourceDTO, result);
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(resourceRepository).getResourceById(1L);
//        verify(userRepository).getUserById(1L);
//        verify(bookingMapper).toBookingWithOwnerResourceDTO(booking, resource, user);
//    }
//
//    @Test
//    @DisplayName("Get User Bookings")
//    void getUserBookings() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//        User user = User.builder().resourceId(1L).username("user").password("password").role("USER").build();
//        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource resourceName", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingsByUserId(1L)).thenReturn(Optional.of(List.of(booking)));
//        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user)).thenReturn(bookingWithOwnerResourceDTO);
//
//        List<BookingWithOwnerResourceDTO> result = bookingService.getUserBookings(1L, req);
//
//        assertEquals(List.of(bookingWithOwnerResourceDTO), result);
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingsByUserId(1L);
//        verify(userRepository).getUserById(1L);
//        verify(resourceRepository).getResourceById(1L);
//        verify(bookingMapper).toBookingWithOwnerResourceDTO(booking, resource, user);
//    }
//
//    @Test
//    @DisplayName("Get Booking By ID - Booking Not Found")
//    void getBookingByIdBookingNotFound() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(1L, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verifyNoInteractions(resourceRepository, userRepository, bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Get Booking By ID - Resource Not Found")
//    void getBookingByIdResourceNotFound() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(resourceRepository).getResourceById(1L);
//        verifyNoInteractions(userRepository, bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Get Booking By ID - User Not Found")
//    void getBookingByIdUserNotFound() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingById(1L, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(resourceRepository).getResourceById(1L);
//        verify(userRepository).getUserById(1L);
//        verifyNoInteractions(bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Get All Bookings")
//    void getAllBookings() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//        Resource resource = Resource.builder().resourceId(1L).resourceName("Resource").resourceType("Type").build();
//        User user = User.builder().resourceId(1L).username("user").password("password").role("USER").build();
//        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource resourceName", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00");
//
//        when(authUtil.authenticate(req, "ADMIN")).thenReturn(currentUser);
//        when(bookingRepository.getAllBookings()).thenReturn(Optional.of(List.of(booking)));
//        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
//        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
//        when(bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user)).thenReturn(bookingWithOwnerResourceDTO);
//
//        List<BookingWithOwnerResourceDTO> result = bookingService.getAllBookings(req);
//
//        assertEquals(List.of(bookingWithOwnerResourceDTO), result);
//        verify(authUtil).authenticate(req, "ADMIN");
//        verify(bookingRepository).getAllBookings();
//        verify(userRepository).getUserById(1L);
//        verify(resourceRepository).getResourceById(1L);
//        verify(bookingMapper).toBookingWithOwnerResourceDTO(booking, resource, user);
//    }
//
//    @Test
//    @DisplayName("Get All Bookings - No Bookings Found")
//    void getAllBookingsNoBookingsFound() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//
//        when(authUtil.authenticate(req, "ADMIN")).thenReturn(currentUser);
//        when(bookingRepository.getAllBookings()).thenReturn(Optional.empty());
//
//        assertThrows(BookingNotFoundException.class, () -> bookingService.getAllBookings(req));
//
//        verify(authUtil).authenticate(req, "ADMIN");
//        verify(bookingRepository).getAllBookings();
//        verifyNoInteractions(userRepository, resourceRepository, bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Update Booking")
//    void updateBooking() {
//        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO(1721477580000L, 1721480400000L);
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking existingBooking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.of(2023, 7, 12, 10, 0)).endTime(LocalDateTime.of(2023, 7, 12, 12, 0)).build();
//        LocalDateTime newStartDateTime = LocalDateTime.of(2024, 7, 20, 14, 0);
//        LocalDateTime newEndDateTime = LocalDateTime.of(2024, 7, 20, 16, 0);
//        Booking updatedBooking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(newStartDateTime).endTime(newEndDateTime).build();
//        BookingDTO updatedBookingDTO = new BookingDTO(1L, 1L, 1L, "2023-07-12T14:00", "2023-07-12T16:00");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
//        when(dateTimeMapper.toLocalDateTime(1721477580000L)).thenReturn(newStartDateTime);
//        when(dateTimeMapper.toLocalDateTime(1721480400000L)).thenReturn(newEndDateTime);
//        when(bookingRepository.updateBooking(existingBooking)).thenReturn(updatedBooking);
//        when(bookingMapper.toDTO(updatedBooking)).thenReturn(updatedBookingDTO);
//
//        BookingDTO result = bookingService.updateBooking(1L, requestDTO, req);
//
//        assertEquals(updatedBookingDTO, result);
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(dateTimeMapper).toLocalDateTime(1721477580000L);
//        verify(dateTimeMapper).toLocalDateTime(1721480400000L);
//        verify(bookingRepository).updateBooking(existingBooking);
//        verify(bookingMapper).toDTO(updatedBooking);
//    }
//
//    @Test
//    @DisplayName("Update Booking - Booking Conflict")
//    void updateBookingConflict() {
//        UpdateBookingRequestDTO requestDTO = new UpdateBookingRequestDTO(1721477580000L, 1721480400000L);
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking existingBooking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.of(2023, 7, 12, 10, 0)).endTime(LocalDateTime.of(2023, 7, 12, 12, 0)).build();
//        LocalDateTime newStartDateTime = LocalDateTime.of(2024, 7, 20, 14, 0);
//        LocalDateTime newEndDateTime = LocalDateTime.of(2024, 7, 20, 16, 0);
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(existingBooking));
//        when(dateTimeMapper.toLocalDateTime(1721477580000L)).thenReturn(newStartDateTime);
//        when(dateTimeMapper.toLocalDateTime(1721480400000L)).thenReturn(newEndDateTime);
//        doThrow(new BookingConflictException("Booking conflict")).when(bookingRepository).updateBooking(existingBooking);
//
//        assertThrows(BookingConflictException.class, () -> bookingService.updateBooking(1L, requestDTO, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(dateTimeMapper).toLocalDateTime(1721477580000L);
//        verify(dateTimeMapper).toLocalDateTime(1721480400000L);
//        verify(bookingRepository).updateBooking(existingBooking);
//        verifyNoInteractions(bookingMapper);
//    }
//
//    @Test
//    @DisplayName("Delete Booking")
//    void deleteBooking() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//        Booking booking = Booking.builder().resourceId(1L).ownerId(1L).resourceId(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build();
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.of(booking));
//
//        bookingService.deleteBooking(1L, req);
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(bookingRepository).deleteBooking(1L);
//    }
//
//    @Test
//    @DisplayName("Delete Booking - Booking Not Found")
//    void deleteBookingNotFound() {
//        UserDTO currentUser = new UserDTO(1L, "user", "USER");
//
//        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
//        when(bookingRepository.getBookingById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(1L, req));
//
//        verify(authUtil).authenticate(req, "USER");
//        verify(bookingRepository).getBookingById(1L);
//        verify(bookingRepository, never()).deleteBooking(anyLong());
//    }
//}
