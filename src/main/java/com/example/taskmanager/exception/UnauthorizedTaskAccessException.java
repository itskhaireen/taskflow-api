package com.example.taskmanager.exception;

public class UnauthorizedTaskAccessException extends RuntimeException {
    public UnauthorizedTaskAccessException(Long taskId) {
        super("Task with ID " + taskId + " does not belong to the user");
    }
} 