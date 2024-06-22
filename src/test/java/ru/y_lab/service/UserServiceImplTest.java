package ru.y_lab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.InputReader;

import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private InputReader inputReader;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        doNothing().when(userRepository).addUser(any(User.class));
        when(inputReader.readLine())
                .thenReturn("username")
                .thenReturn("password");
    }

    @Test
    public void testRegisterUser_Success() {
        User mockedUser = new User("123", "validUsername", "validPassword@123", "USER");

        userService.registerUser();

        verify(userRepository, times(1)).addUser(any(User.class));
    }

}
