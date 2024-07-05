package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;

import java.io.IOException;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {

    /**
     * Adds a new resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void addResource(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     * @throws IOException if an I/O error occurs
     */
    void getResourceById(HttpServletRequest req, HttpServletResponse resp) throws ResourceNotFoundException, IOException;

    /**
     * Retrieves a list of all resources.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws UserNotFoundException if the user is not found
     * @throws IOException if an I/O error occurs
     */
    void getAllResources(HttpServletRequest req, HttpServletResponse resp) throws UserNotFoundException, IOException;

    /**
     * Updates an existing resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void updateResource(HttpServletRequest req, HttpServletResponse resp) throws IOException;


    /**
     * Deletes a resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
