package com.javaTraining.session4.controller;

import com.javaTraining.session4.dto.TodoDTO;
import com.javaTraining.session4.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        TodoDTO createdTodo = todoService.createTodo(todoDTO);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        List<TodoDTO> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
        TodoDTO todo = todoService.getTodoById(id);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        TodoDTO updatedTodo = todoService.updateTodo(id, todoDTO);
        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return new ResponseEntity<>("Todo item deleted", HttpStatus.OK);
    }
}
