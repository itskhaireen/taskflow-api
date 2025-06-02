package com.example.taskmanager;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Task task;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Clear existing data
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save test user
        user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user = userRepository.save(user);

        // Create and save test task
        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(user.getId());
        task = taskRepository.save(task);

        // Get user details and generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        jwtToken = TestJwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void getAllTasks_ShouldReturnUserTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(task.getId()))
            .andExpect(jsonPath("$[0].title").value(task.getTitle()));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task.getId()))
            .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    void createTask_ShouldCreateNewTask() throws Exception {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(newTask.getTitle()))
            .andExpect(jsonPath("$.description").value(newTask.getDescription()));
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(updatedTask.getTitle()))
            .andExpect(jsonPath("$.description").value(updatedTask.getDescription()));
    }

    @Test
    void updateTaskStatus_ShouldUpdateTaskStatus() throws Exception {
        mockMvc.perform(patch("/api/tasks/{id}/status", task.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\": true}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk());

        // Verify task is deleted
        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isNotFound());
    }
} 