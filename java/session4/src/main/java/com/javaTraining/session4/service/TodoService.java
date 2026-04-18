package com.javaTraining.session4.service;

import com.javaTraining.session4.dto.TodoDTO;
import com.javaTraining.session4.exception.TodoCustomException;
import com.javaTraining.session4.entity.Todo;
import com.javaTraining.session4.entity.TodoStatus;
import com.javaTraining.session4.repository.TodoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoDTO createTodo(TodoDTO todoDTO) {
        Todo newTodo = new Todo();
        newTodo.setTitle(todoDTO.getTitle());
        newTodo.setDescription(todoDTO.getDescription());
        newTodo.setCreatedAt(LocalDateTime.now());
        if (todoDTO.getStatus() != null) {
            newTodo.setStatus(todoDTO.getStatus());
        } else {
            newTodo.setStatus(TodoStatus.PENDING);
        }

        Todo savedTodo = todoRepository.save(newTodo);

        return convertToDTO(savedTodo);
    }

    public List<TodoDTO> getAllTodos() {
        List<Todo> allTodos = todoRepository.findAll();
        List<TodoDTO> todoDTOList = new ArrayList<>();

        for (Todo todo : allTodos) {
            todoDTOList.add(convertToDTO(todo));
        }

        return todoDTOList;
    }

    public TodoDTO getTodoById(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new TodoCustomException("Todo item with ID " + id + " was not found."));

        return convertToDTO(todo);
    }

    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        Todo existingTodo = todoRepository.findById(id).orElseThrow(
                () -> new TodoCustomException("Cannot update. Todo item with ID " + id + " was not found."));

        if (todoDTO.getTitle() != null) {
            existingTodo.setTitle(todoDTO.getTitle());
        }
        if (todoDTO.getDescription() != null) {
            existingTodo.setDescription(todoDTO.getDescription());
        }

        if (todoDTO.getStatus() != null) {
            if (existingTodo.getStatus() == todoDTO.getStatus()) {
                throw new IllegalArgumentException(
                        "The task is already marked as " + todoDTO.getStatus());
            }

            existingTodo.setStatus(todoDTO.getStatus());
        }

        Todo updatedTodo = todoRepository.save(existingTodo);

        return convertToDTO(updatedTodo);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoCustomException("Cannot delete. Todo item with ID " + id + " was not found.");
        }
        todoRepository.deleteById(id);
    }

    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        return dto;
    }
}
