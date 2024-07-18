package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.dto.UserAuthDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepo;
import ru.y_lab.service.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepo userRepo;

    public UserAuthDTO loadUserByUsername(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow( () -> new UserNotFoundException("User not found with username: " + username));

        return new UserAuthDTO(user.getUsername(), user.getPassword());
    }
}
