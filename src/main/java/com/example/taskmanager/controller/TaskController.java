package com.example.taskmanager.controller; // A controller handles HTTP requests (GET, POST)

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Makes this a RESTful API Controller
@RequestMapping("/api/tasks") // All endpoints start with /api/tasks
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.getAllTasks(completed, sortBy, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.getTaskById(id, userId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.createTask(task, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task, @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.updateTask(id, task, userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request, @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request.completed(), userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok().build();
    }
}

record StatusUpdateRequest(boolean completed) {}