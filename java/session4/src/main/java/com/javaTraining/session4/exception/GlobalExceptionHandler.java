package com.javaTraining.session4.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception e) {
        return "An error occurred: " + e.getMessage();
    }
}
