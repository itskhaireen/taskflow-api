package com.example.taskmanager;

import com.example.taskmanager.config.TestSecurityConfig;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedTaskAccessException;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.controller.TaskController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(TestSecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    private Task task1;
    private User user;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");

        // Create test task
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Test Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);
        task1.setUserId(user.getId());

        // Mock user repository behavior
        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(user));

        // Mock UserDetailsService
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername("testuser")
            .password("password123")
            .authorities("ROLE_USER")
            .build();
        when(userDetailsService.loadUserByUsername("testuser"))
            .thenReturn(userDetails);

        // Generate JWT token
        jwtToken = TestJwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void getAllTasks_ShouldReturnTasks() throws Exception {
        // Arrange
        when(taskService.getAllTasks(null, "id", user.getId()))
            .thenReturn(Arrays.asList(task1));

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(task1.getId()))
            .andExpect(jsonPath("$[0].title").value(task1.getTitle()))
            .andExpect(jsonPath("$[0].description").value(task1.getDescription()))
            .andExpect(jsonPath("$[0].completed").value(task1.isCompleted()));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        // Arrange
        when(taskService.getTaskById(task1.getId(), user.getId()))
            .thenReturn(task1);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", task1.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task1.getId()))
            .andExpect(jsonPath("$.title").value(task1.getTitle()));
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(taskService.getTaskById(999L, user.getId()))
            .thenThrow(new TaskNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", 999L)
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");

        when(taskService.createTask(any(Task.class), eq(user.getId())))
            .thenReturn(task1);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask))
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task1.getId()))
            .andExpect(jsonPath("$.title").value(task1.getTitle()));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTask() throws Exception {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        when(taskService.updateTask(eq(task1.getId()), any(Task.class), eq(user.getId())))
            .thenReturn(task1);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{id}", task1.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask))
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task1.getId()));
    }

    @Test
    void updateTaskStatus_WhenTaskExists_ShouldReturnUpdatedTask() throws Exception {
        // Arrange
        when(taskService.updateTaskStatus(eq(task1.getId()), eq(true), eq(user.getId())))
            .thenReturn(task1);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status", task1.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\": true}")
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(task1.getId()));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(task1.getId(), user.getId());

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", task1.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isOk());
    }

    @Test
    void deleteTask_WhenTaskNotFound_ShouldReturn404() throws Exception {
        // Arrange
        doThrow(new TaskNotFoundException(999L))
            .when(taskService).deleteTask(999L, user.getId());

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", 999L)
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_WhenUnauthorized_ShouldReturn403() throws Exception {
        // Arrange
        doThrow(new UnauthorizedTaskAccessException(task1.getId()))
            .when(taskService).deleteTask(task1.getId(), user.getId());

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", task1.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .requestAttr("userId", user.getId()))
            .andExpect(status().isForbidden());
    }
} 