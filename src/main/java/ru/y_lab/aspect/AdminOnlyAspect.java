package ru.y_lab.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.service.JWTService;

import static ru.y_lab.enums.RoleType.ADMIN;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminOnlyAspect {

    private final JWTService jwtService;
    private final HttpServletRequest request;

    @Before("@annotation(ru.y_lab.annotation.AdminOnly)")
    public void checkAdminRole() {
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7).trim();

        if (!jwtService.hasRole(token, ADMIN.name())) {
            throw new AuthorizationException("Access denied. Only admin users can access this resource.");
        }
    }
}
