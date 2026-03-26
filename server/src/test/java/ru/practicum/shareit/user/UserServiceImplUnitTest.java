package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {
    @Mock
    private UserRepository userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private User updateUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@email.com")
                .build();

        updateUser = User.builder()
                .id(1L)
                .name("New Name")
                .email("new@email.com")
                .build();
    }

    @Test
    void shouldUpdateUserNameAndEmailWhenBothAreProvided() {
        when(userStorage.findUserByEmail("new@email.com")).thenReturn(Optional.empty());
        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("new@email.com", result.getEmail());

        verify(userStorage).findUserByEmail("new@email.com");
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void shouldUpdateOnlyNameWhenEmailIsNull() {
        updateUser.setEmail(null);
        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());

        verify(userStorage, never()).findUserByEmail(anyString());
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void shouldUpdateOnlyEmailWhenNameIsNull() {
        updateUser.setName(null);
        when(userStorage.findUserByEmail("new@email.com")).thenReturn(Optional.empty());
        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("new@email.com", result.getEmail());

        verify(userStorage).findUserByEmail("new@email.com");
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExistsForDifferentUser() {
        User differentUser = User.builder()
                .id(2L)
                .name("Different User")
                .email("new@email.com")
                .build();

        when(userStorage.findUserByEmail("new@email.com")).thenReturn(Optional.of(differentUser));

        assertThrows(DuplicateException.class, () -> userService.updateUser(updateUser));

        verify(userStorage).findUserByEmail("new@email.com");
        verify(userStorage, never()).findById(anyLong());
        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    void shouldNotThrowDuplicateExceptionWhenEmailExistsForSameUser() {
        updateUser.setEmail("old@email.com");
        when(userStorage.findUserByEmail("old@email.com")).thenReturn(Optional.of(existingUser));
        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> userService.updateUser(updateUser));

        verify(userStorage).findUserByEmail("old@email.com");
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void shouldReturnSameUserWhenNoUpdatesAreProvided() {
        updateUser.setName(null);
        updateUser.setEmail(null);

        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userStorage, never()).findUserByEmail(anyString());
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void shouldHandleUserNotFoundInStorage() {
        when(userStorage.findUserByEmail("new@email.com")).thenReturn(Optional.empty());
        when(userStorage.findById(1L)).thenReturn(Optional.empty());

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("new@email.com", result.getEmail());

        verify(userStorage).findUserByEmail("new@email.com");
        verify(userStorage).findById(1L);
        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWhenOnlyEmailProvidedAndItIsSame() {
        updateUser.setName(null);

        when(userStorage.findUserByEmail("new@email.com")).thenReturn(Optional.empty());
        when(userStorage.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(updateUser);

        assertNotNull(result);
        assertEquals("new@email.com", result.getEmail());

        verify(userStorage).findUserByEmail("new@email.com");
        verify(userStorage).findById(1L);
        verify(userStorage).save(any(User.class));
    }


}
