package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedTaskAccessException;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks(Boolean completed, String sortBy, Long userId) {
        if (completed == null) {
            if ("title".equals(sortBy)) {
                return taskRepository.findByUserIdOrderByTitle(userId);
            } else {
                return taskRepository.findByUserIdOrderById(userId);
            }
        } else {
            return taskRepository.findByUserIdAndCompletedOrderById(userId, completed);
        }
    }

    @Override
    public Task getTaskById(Long id, Long userId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException(id);
        }
        return task;
    }

    @Override
    public Task createTask(Task task, Long userId) {
        task.setUserId(userId);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task updatedTask, Long userId) {
        Task task = getTaskById(id, userId);
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        return taskRepository.save(task);
    }

    @Override
    public Task updateTaskStatus(Long id, boolean completed, Long userId) {
        Task task = getTaskById(id, userId);
        task.setCompleted(completed);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id, Long userId) {
        Task task = getTaskById(id, userId);
        taskRepository.deleteById(task.getId());
    }
} 