package ru.y_lab.ui;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.service.UserService;

import java.util.List;

public interface ResourceUI {

    void showResourceMenu(UserService userService);

    void showAvailableResources(List<Resource> resources, UserService userService) throws UserNotFoundException;
}
