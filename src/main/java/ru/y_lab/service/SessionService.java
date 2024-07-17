package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.y_lab.dto.UserDTO;

public interface SessionService {

    /**
     * Creates a session and stores the authenticated user in it.
     *
     * @param user the authenticated user
     * @param request the HTTP request to get the session
     */
    public void createSession(UserDTO user, HttpServletRequest request);

    /**
     * Invalidates the current user session.
     *
     * <p>This method retrieves the current {@link HttpSession} from the provided {@link HttpServletRequest}
     * and invalidates it, effectively logging out the user. If there is no current session,
     * the method does nothing.</p>
     *
     * @param httpRequest the {@link HttpServletRequest} from which to retrieve the session
     * @throws IllegalStateException if the session has already been invalidated
     */
    public void shutdownSession(HttpServletRequest httpRequest);
}
