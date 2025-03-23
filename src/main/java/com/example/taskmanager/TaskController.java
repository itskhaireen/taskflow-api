package com.example.taskmanager; // A controller handles HTTP requests (GET, POST)

import org.springframework.security.core.context.SecurityContextHolder;
// Spring annotations for REST from the Spring Web Library (included via Maven)
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // Makes this a RESTful API Controller
@RequestMapping("/api/tasks") // All endpoints start with /api/tasks
public class TaskController {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository repository, UserRepository userRepository){
        this.repository = repository;
        this.userRepository = userRepository;
        // Spring auto-injects, but explicit mentioning of what to inject here.
    }

    // Handles GET api/tasks
    @GetMapping
    public List<Task> getAll(
            @RequestParam(value = "completed", required = false) Boolean completed,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
        if (completed != null) {
            return repository.findByUserIdAndCompleted(user.getId(), completed);
        }
        if ("title".equalsIgnoreCase(sortBy)) {
            return repository.findByUserIdOrderByTitle(user.getId());
        }
        return repository.findByUserIdOrderById(user.getId());
    }


    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        Task task = repository.findById(id).orElseThrow(() -> new RuntimeException("Task Not Found With ID: " + id));

        if (!task.getUserId().equals(user.getId())) {
            throw new RuntimeException("Task Does Not Belong To The User");
        }
        return task;
    }
    

    // Handles POST api/tasks
    @PostMapping
    public Task create(@RequestBody Task task) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        task.setUserId(user.getId());
        return repository.save(task); // Saves all tasks
    }

    // Handles PUT api/tasks
    // Let users update an existing task by sending a PUT request with the task's ID and new details
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task updatedTask) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
        Task existingTask = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task Not Found With ID: " + id));

        if (!existingTask.getUserId().equals(user.getId())){
            throw new RuntimeException("Task Does Not Belong to the User!");
        }
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());
        
        return repository.save(existingTask);

        // updatedTask = an instance of the Task object for update @ put
        // existingTask = an instance of the Task object to get the data of id and body
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        Task task = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task Not Found With ID: " + id));
        if(!task.getUserId().equals(user.getId())) {
            throw new RuntimeException("Task Does Not Belong To User");
        }

        task.setCompleted(request.completed());
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        Task task = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task Not Found With ID: " + id));
        if(!task.getId().equals(user.getId())){
            throw new RuntimeException("Task Does Not Belong to the User!");
        }
        repository.deleteById(id);
    }
   
}

record StatusUpdateRequest(boolean completed) {}