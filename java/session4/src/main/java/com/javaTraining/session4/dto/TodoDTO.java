package com.javaTraining.session4.dto;

import com.javaTraining.session4.entity.TodoStatus;

import jakarta.validation.constraints.NotNull;

public class TodoDTO {

    @NotNull(message = "Title cannot be null")
    private String title;

    private String description;

    private TodoStatus status;

    public TodoDTO() {
    }

    public TodoDTO(String title, String description, TodoStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }
}
