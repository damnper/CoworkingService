package ru.y_lab.ui;

import ru.y_lab.service.UserService;
import ru.y_lab.util.InputReader;

public interface BookingUI {

    void showBookingMenu(UserService userService);

    void showFilterMenu(UserService userService, InputReader inputReader);

}
