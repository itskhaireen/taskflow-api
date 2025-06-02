package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskService {
    List<Task> getAllTasks(Boolean completed, String sortBy, Long userId);
    Task getTaskById(Long id, Long userId);
    Task createTask(Task task, Long userId);
    Task updateTask(Long id, Task task, Long userId);
    Task updateTaskStatus(Long id, boolean completed, Long userId);
    void deleteTask(Long id, Long userId);
} 