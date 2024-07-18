package ru.y_lab.service;

import ru.y_lab.dto.UserAuthDTO;

public interface CustomUserDetailsService {

    UserAuthDTO loadUserByUsername(String username);

}
