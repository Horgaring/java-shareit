package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImpTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User testUser;
    private ItemDto testItemDto;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testItemDto = new ItemDto();
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);
        testItemDto.setUserId(1L);

        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        testItem.setUser(testUser);
    }

    @Test
    void testAddItem_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.addItem(testItemDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());

        verify(userService, times(1)).getUserById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(commentRepository, times(1)).findAllByItemId(1L);
    }

    @Test
    void testAddItem_WithComments() {
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Great item!");
        testComment.setAuthor(testUser);
        testComment.setItem(testItem);
        testComment.setCreated(LocalDateTime.now());

        CommentDto expectedCommentDto = new CommentDto();
        expectedCommentDto.setId(1L);
        expectedCommentDto.setText("Great item!");
        expectedCommentDto.setAuthorName("Test User");
        expectedCommentDto.setCreated(testComment.getCreated());

        Comment commentSpy = spy(testComment);
        when(commentSpy.toCommentDto()).thenReturn(expectedCommentDto);

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(commentSpy));

        ItemDto result = itemService.addItem(testItemDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Great item!", result.getComments().get(0).getText());

        verify(userService, times(1)).getUserById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(commentRepository, times(1)).findAllByItemId(1L);
    }

    @Test
    void testAddItem_UserNotFound() {
        when(userService.getUserById(999L)).thenThrow(new NotFoundException("User not found"));
        testItemDto.setUserId(999L);

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(testItemDto));

        verify(userService, times(1)).getUserById(999L);
        verify(itemRepository, never()).save(any(Item.class));
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void testAddItem_SavesCorrectUser() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(1L);
            assertEquals(testUser, savedItem.getUser());
            return savedItem;
        });
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());

        ItemDto result = itemService.addItem(testItemDto);

        assertNotNull(result);
        verify(itemRepository, times(1)).save(argThat(item ->
                item.getUser().equals(testUser) &&
                        item.getName().equals("Test Item")
        ));
    }

    @Test
    public void shouldReturnEmptyListWhenTextIsNull() {
        assertEquals(Collections.emptyList(), itemService.searchItems(null));
    }

    @Test
    public void shouldReturnListWhenTextIsNotNullAndNotBlank() {

        when(itemRepository.searchItemByNameAndDescription(any(String.class)))
                .thenReturn(List.of(testItem));
        assertEquals(1, itemService.searchItems("  1").size());
    }


}