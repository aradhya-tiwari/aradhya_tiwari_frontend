package com.javaTraining.session4.controller;

import com.javaTraining.session4.dto.TodoDTO;
import com.javaTraining.session4.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        logger.info("Received request to create a new TODO: {}", todoDTO.getTitle());
        TodoDTO createdTodo = todoService.createTodo(todoDTO);
        logger.info("Successfully processed request to create TODO.");
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        logger.info("Received request to fetch all TODOs");
        List<TodoDTO> todos = todoService.getAllTodos();
        logger.info("Successfully fetched {} TODOs", todos.size());
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        logger.info("Received request to fetch TODO with ID: {}", id);
        TodoDTO todo = todoService.getTodoById(id);
        logger.info("Successfully fetched TODO with ID: {}", id);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        logger.info("Received request to update TODO with ID: {}", id);
        TodoDTO updatedTodo = todoService.updateTodo(id, todoDTO);
        logger.info("Successfully updated TODO with ID: {}", id);
        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        logger.info("Received request to delete TODO with ID: {}", id);
        todoService.deleteTodo(id);
        logger.info("Successfully deleted TODO with ID: {}", id);
        return new ResponseEntity<>("Todo item deleted", HttpStatus.OK);
    }
}
