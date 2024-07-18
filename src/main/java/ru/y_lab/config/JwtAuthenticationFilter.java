package ru.y_lab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.y_lab.dto.ErrorResponse;
import ru.y_lab.dto.UserAuthDTO;
import ru.y_lab.exception.JwtAuthenticationException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.CustomUserDetailsService;
import ru.y_lab.service.JWTService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NonNullApi
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    private static final String[] AUTH_WHITELIST = {
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/v1/users/register",
            "/api/v1/users/login" };

    private final PathMatcher pathMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isWhitelisted(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JwtAuthenticationException("You are not logged in. Please log in to access this resource.");
            }

            final String jwt = authHeader.substring(7).trim();
            final String username = jwtService.extractUserName(jwt);

            if (username != null && request.getAttribute("authenticatedUser") == null) {
                UserAuthDTO userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails.username())) {
                    request.setAttribute("authenticatedUser", userDetails);
                } else {
                    throw new JwtAuthenticationException("Token is not valid");
                }
            }

            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException | UserNotFoundException ex) {
            setUnauthorizedResponse(response, ex.getMessage());
        }
    }

    private boolean isWhitelisted(HttpServletRequest request) {
        for (String pattern : AUTH_WHITELIST) {
            if (pathMatcher.match(pattern, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                message,
                System.currentTimeMillis()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
