package ru.y_lab.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.repo.BookingRepo;
import ru.y_lab.repo.ResourceRepo;
import ru.y_lab.service.JWTService;

@Aspect
@Component
@RequiredArgsConstructor
public class UserOwnsResourceAspect {

    private final JWTService jwtService;
    private final ResourceRepo resourceRepo;
    private final BookingRepo bookingRepo;

    @Pointcut("@annotation(ru.y_lab.annotation.AdminOrOwner)")
    public void adminOrOwnerPointcut() {}

    @Before(value = "adminOrOwnerPointcut() && args(token, resourceId, ..)", argNames = "token,resourceId")
    public void checkAdminOrOwner(String token, Long resourceId) {
        Long userIdFromToken = jwtService.extractUserId(token);
        String userRole = jwtService.extractUserRole(token);
        Long userIdFromResource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The resource could not be found. Please check the ID and try again."))
                .getUserId();

        if (!userIdFromToken.equals(userIdFromResource) && !"ADMIN".equals(userRole)) {
            throw new AuthorizationException("Access denied. You do not have permission to modify this resource.");
        }
    }

    @Before(value = "adminOrOwnerPointcut() && args(token, bookingId, ..)", argNames = "token,bookingId")
    public void checkAdminOrOwnerBooking(String token, Long bookingId) {
        Long userIdFromToken = jwtService.extractUserId(token);
        String userRole = jwtService.extractUserRole(token);
        Long userIdFromBooking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The booking could not be found. Please check the ID and try again."))
                .getUserId();

        if (!userIdFromToken.equals(userIdFromBooking) && !"ADMIN".equals(userRole)) {
            throw new AuthorizationException("Access denied. You do not have permission to access this user's bookings.");
        }
    }
}
