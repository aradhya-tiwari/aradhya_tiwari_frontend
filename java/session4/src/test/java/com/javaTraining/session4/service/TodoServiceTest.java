package com.javaTraining.session4.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.javaTraining.session4.dto.TodoDTO;
import com.javaTraining.session4.entity.Todo;
import com.javaTraining.session4.entity.TodoStatus;
import com.javaTraining.session4.exception.TodoCustomException;
import com.javaTraining.session4.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    // Injecting mock repository
    @Mock
    private TodoRepository todoRepository;

    // Injection mock notification dependency to the service class
    @Mock
    private NotificationServiceClient notificationServiceClient;

    // Injection mock todo dependency to the service class
    @InjectMocks
    private TodoService todoService;

    private Todo todo;
    private TodoDTO todoDTO;

    // This will run before each test case to set up common objects and state
    @BeforeEach
    void setUp() {
        todo = new Todo();
        todo.setId(12L);
        todo.setTitle("Test Task");
        todo.setDescription("Test Description");
        todo.setStatus(TodoStatus.PENDING);
        todo.setCreatedAt(LocalDateTime.now());

        todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Task");
        todoDTO.setDescription("Test Description");
        todoDTO.setStatus(TodoStatus.PENDING);
    }

    @Test
    void testCreateTodo() {
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoDTO createdTodo = todoService.createTodo(todoDTO);

        assertNotNull(createdTodo);
        assertEquals("Test Task", createdTodo.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(notificationServiceClient, times(1)).sendNotification(anyString());
    }

    @Test
    void testCreateTodoWithNullStatus() {
        todoDTO.setStatus(null);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoDTO createdTodo = todoService.createTodo(todoDTO);

        assertNotNull(createdTodo);
        assertEquals(TodoStatus.PENDING, createdTodo.getStatus());
    }

    @Test
    void testGetAllTodos() {
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo));

        List<TodoDTO> todos = todoService.getAllTodos();

        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void testGetTodoById() {
        when(todoRepository.findById(12L)).thenReturn(Optional.of(todo));

        TodoDTO foundTodo = todoService.getTodoById(12L);

        assertNotNull(foundTodo);
        assertEquals("Test Task", foundTodo.getTitle());
    }

    @Test
    void testGetTodoByIdNotFound() {
        when(todoRepository.findById(12L)).thenReturn(Optional.empty());

        assertThrows(TodoCustomException.class, () -> todoService.getTodoById(12L));
    }

    @Test
    void testUpdateTodo() {
        when(todoRepository.findById(12L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        todoDTO.setStatus(TodoStatus.COMPLETED); // Valid

        TodoDTO updatedTodo = todoService.updateTodo(12L, todoDTO);

        assertNotNull(updatedTodo);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testUpdateTodoInvalidTransition() {
        when(todoRepository.findById(12L)).thenReturn(Optional.of(todo));

        todoDTO.setStatus(TodoStatus.PENDING); // Throws because its already PENDING

        assertThrows(IllegalArgumentException.class, () -> todoService.updateTodo(12L, todoDTO));
    }

    @Test
    void testDeleteTodo() {
        when(todoRepository.existsById(12L)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(12L);

        todoService.deleteTodo(12L);

        verify(todoRepository, times(1)).deleteById(12L);
    }

    @Test
    void testDeleteTodoNotFound() {
        when(todoRepository.existsById(12L)).thenReturn(false);

        assertThrows(TodoCustomException.class, () -> todoService.deleteTodo(12L));
    }
}
