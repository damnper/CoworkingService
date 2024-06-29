package ru.y_lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.UserServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CoworkingServiceApp}.
 */
@DisplayName("Unit Tests for CoworkingServiceAppTest")
public class CoworkingServiceAppTest {


    @BeforeEach
    public void setUp() {
        UserService userService = mock(UserServiceImpl.class);

        when(userService.getCurrentUser()).thenReturn(null);
    }

    @Test
    public void testMainMethod() {
        String simulatedUserInput = "0\n";
        InputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);

        CoworkingServiceApp.main(new String[]{});

    }
}
