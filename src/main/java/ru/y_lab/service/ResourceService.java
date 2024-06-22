package ru.y_lab.service;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {

    void manageResources() throws ResourceNotFoundException, UserNotFoundException;

    void viewResources() throws UserNotFoundException;

    Resource getResourceById(String ig) throws ResourceNotFoundException;

}