package com.javaTraining.session4.exception;

public class TodoCustomException extends RuntimeException {
    // custom exception to handle not found and other errors related to TODO
    // operations
    public TodoCustomException(String message) {
        super(message);
    }
}
