package ru.y_lab.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.impl.ActionExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for ActionExecutorTest")
public class ActionExecutorTest {

    @Mock
    private UserService userService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private BookingService bookingService;

    private ActionExecutor actionExecutor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        actionExecutor = new ActionExecutor(userService, resourceService, bookingService);
    }

    /**
     * Тестирование выполнения действия по выбору для зарегистрированного пользователя.
     * <p>
     * Этот тест проверяет, что при выборе опции 1 для зарегистрированного пользователя вызывается метод `registerUser`.
     * </p>
     * @throws BookingConflictException если возникает конфликт бронирования
     * @throws ResourceNotFoundException если ресурс не найден
     * @throws UserNotFoundException если пользователь не найден
     */
    @Test
    @DisplayName("Test Execute Choice for User Register")
    public void testExecuteChoice_UserRegister() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "username", "password", "USER"));

        actionExecutor.executeChoice(1);

        verify(userService).registerUser();
    }

    /**
     * Тестирование выполнения действия по выбору для администратора.
     * <p>
     * Этот тест проверяет, что при выборе опции 3 для администратора вызывается метод `viewAllUsers`.
     * </p>
     * @throws BookingConflictException если возникает конфликт бронирования
     * @throws ResourceNotFoundException если ресурс не найден
     * @throws UserNotFoundException если пользователь не найден
     */
    @Test
    @DisplayName("Test Execute Choice for Admin to View All Users")
    public void testExecuteChoice_AdminViewAllUsers() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "admin", "password", "ADMIN"));

        actionExecutor.executeChoice(3);

        verify(userService).viewAllUsers();
    }

    /**
     * Тестирование выхода из приложения при выборе опции 0.
     * <p>
     * Этот тест проверяет, что при выборе опции 0 вызывается метод `exitApplication` и приложение завершает выполнение.
     * </p>
     * @throws BookingConflictException если возникает конфликт бронирования
     * @throws ResourceNotFoundException если ресурс не найден
     * @throws UserNotFoundException если пользователь не найден
     */
    @Test
    @DisplayName("Test Execute Choice to Exit Application")
    public void testExecuteChoice_ExitApplication() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        actionExecutor.executeChoice(0);

        assertFalse(actionExecutor.running);
    }

    /**
     * Тестирование обработки некорректного выбора.
     * <p>
     * Этот тест проверяет, что при выборе некорректной опции вызывается метод `invalidChoice`.
     * </p>
     * @throws BookingConflictException если возникает конфликт бронирования
     * @throws ResourceNotFoundException если ресурс не найден
     * @throws UserNotFoundException если пользователь не найден
     */
    @Test
    @DisplayName("Test Execute Choice for Invalid Option")
    public void testExecuteChoice_InvalidChoice() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        ActionExecutor spyExecutor = spy(actionExecutor);
        when(userService.getCurrentUser()).thenReturn(new User(1L, "username", "password", "USER"));

        spyExecutor.executeChoice(99);

        verify(spyExecutor).invalidChoice();
    }
}
