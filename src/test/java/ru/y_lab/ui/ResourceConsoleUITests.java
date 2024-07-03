package ru.y_lab.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.impl.ResourceConsoleUI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ResourceConsoleUITests {

    @Mock
    private UserService userService;

    @InjectMocks
    private ResourceConsoleUI resourceConsoleUI;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Show Resource Menu")
    public void testShowResourceMenu() {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "testUser", "password", "USER"));

        resourceConsoleUI.showResourceMenu(userService);

        verify(userService, times(1)).getCurrentUser();
        verifyNoMoreInteractions(userService);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        resourceConsoleUI.showResourceMenu(userService);
        String output = outContent.toString();
        assertTrue(output.contains("--- Manage Resources ---"));
    }

    @Test
    @DisplayName("Test Show Available Resources When No Resources Available")
    public void testShowAvailableResources_noResources() throws UserNotFoundException {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

        resourceConsoleUI.showAvailableResources(Collections.emptyList(), userService);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        resourceConsoleUI.showAvailableResources(Collections.emptyList(), userService);
        String output = outContent.toString();
        assertTrue(output.contains("--- Available Resources ---"));
        assertTrue(output.contains("----------------------------"));
    }

    @Test
    @DisplayName("Test Show Available Resources With Resources")
    public void testShowAvailableResources_withResources() throws UserNotFoundException {
        User user = new User(1L, "testUser", "password", "USER");
        Resource resource = new Resource(1L, 1L, "Test Resource", "Type A");
        when(userService.getUserById(anyLong())).thenReturn(user);

        List<Resource> resources = List.of(resource);

        resourceConsoleUI.showAvailableResources(resources, userService);

        verify(userService, times(1)).getUserById(anyLong());
        verifyNoMoreInteractions(userService);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        resourceConsoleUI.showAvailableResources(resources, userService);
        String output = outContent.toString();
        assertTrue(output.contains("--- Available Resources ---"));
        assertTrue(output.contains("Resource ID   : 1"));
        assertTrue(output.contains("Organizer     : testUser"));
        assertTrue(output.contains("Resource Name : Test Resource"));
        assertTrue(output.contains("Resource Type : Type A"));
        assertTrue(output.contains("----------------------------"));
    }

    @Test
    @DisplayName("Test Show Available Resources When User Not Found")
    public void testShowAvailableResources_userNotFound() throws UserNotFoundException {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

        List<Resource> resources = List.of(new Resource(1L, 1L, "Test Resource", "Type A"));

        assertThrows(UserNotFoundException.class, () -> resourceConsoleUI.showAvailableResources(resources, userService));
    }
}
