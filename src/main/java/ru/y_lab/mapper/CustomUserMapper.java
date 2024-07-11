package ru.y_lab.mapper;

import org.springframework.stereotype.Component;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.model.User;

@Component
public class CustomUserMapper {

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        return User.builder()
                .id(userDTO.id())
                .username(userDTO.username())
                .role(userDTO.role())
                .build();
    }

    public UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
