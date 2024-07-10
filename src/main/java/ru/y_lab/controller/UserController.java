package ru.y_lab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO request) {
        UserDTO userDTO = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping(value ="/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO request) throws UserNotFoundException {
        UserDTO userDTO = userService.loginUser(request);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) throws UserNotFoundException {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequestDTO request) throws UserNotFoundException {
        UserDTO userDTO = userService.updateUser(userId, request);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) throws UserNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
