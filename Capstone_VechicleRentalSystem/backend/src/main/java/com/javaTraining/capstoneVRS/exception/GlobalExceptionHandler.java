package com.javaTraining.capstoneVRS.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception e) {
        return "An Error occured \n" + e.getMessage();
    }
}
