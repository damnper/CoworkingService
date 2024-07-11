package ru.y_lab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
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
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        UserDTO userDTO = userService.loginUser(request, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest) {
        List<UserDTO> users = userService.getAllUsers(httpRequest);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequestDTO updateRequest, HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.updateUser(userId, updateRequest, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId,  HttpServletRequest httpRequest) {
        userService.deleteUser(userId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
