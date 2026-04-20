package com.javaTraining.session4.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaTraining.session4.dto.TodoDTO;
import com.javaTraining.session4.entity.TodoStatus;
import com.javaTraining.session4.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoDTO todoDTO;

    @BeforeEach
    void setUp() {
        todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Task");
        todoDTO.setDescription("Test Description");
        todoDTO.setStatus(TodoStatus.PENDING);
    }

    @Test
    void testCreateTodo() throws Exception {
        when(todoService.createTodo(any(TodoDTO.class))).thenReturn(todoDTO);

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testCreateTodoValidationFailure() throws Exception {
        // Create an invalid DTO (title is too short)
        TodoDTO invalidDTO = new TodoDTO();
        invalidDTO.setTitle("ab");

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllTodos() throws Exception {
        when(todoService.getAllTodos()).thenReturn(Arrays.asList(todoDTO));

        mockMvc.perform(get("/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void testGetTodoById() throws Exception {
        when(todoService.getTodoById(12L)).thenReturn(todoDTO);

        mockMvc.perform(get("/todos/12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        when(todoService.updateTodo(eq(12L), any(TodoDTO.class))).thenReturn(todoDTO);

        mockMvc.perform(put("/todos/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/todos/12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Todo item deleted"));
    }
}
