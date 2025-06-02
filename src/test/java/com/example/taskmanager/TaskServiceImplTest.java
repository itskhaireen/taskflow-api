package com.example.taskmanager;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedTaskAccessException;
import com.example.taskmanager.service.TaskServiceImpl;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private TaskServiceImpl taskService;
    private User user;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);

        // Create test user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Create test tasks
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);
        task1.setUserId(user.getId());

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);
        task2.setUserId(user.getId());
    }

    @Test
    void getAllTasks_WhenCompletedIsNull_ShouldReturnAllTasks() {
        // Arrange
        when(taskRepository.findByUserIdOrderById(user.getId()))
            .thenReturn(Arrays.asList(task1, task2));

        // Act
        List<Task> tasks = taskService.getAllTasks(null, "id", user.getId());

        // Assert
        assertEquals(2, tasks.size());
        verify(taskRepository).findByUserIdOrderById(user.getId());
    }

    @Test
    void getAllTasks_WhenCompletedIsTrue_ShouldReturnCompletedTasks() {
        // Arrange
        when(taskRepository.findByUserIdAndCompletedOrderById(user.getId(), true))
            .thenReturn(Arrays.asList(task2));

        // Act
        List<Task> tasks = taskService.getAllTasks(true, "id", user.getId());

        // Assert
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isCompleted());
        verify(taskRepository).findByUserIdAndCompletedOrderById(user.getId(), true);
    }

    @Test
    void getAllTasks_WhenSortByTitle_ShouldReturnTasksSortedByTitle() {
        // Arrange
        when(taskRepository.findByUserIdOrderByTitle(user.getId()))
            .thenReturn(Arrays.asList(task1, task2));

        // Act
        List<Task> tasks = taskService.getAllTasks(null, "title", user.getId());

        // Assert
        assertEquals(2, tasks.size());
        verify(taskRepository).findByUserIdOrderByTitle(user.getId());
    }

    @Test
    void getTaskById_WhenTaskExistsAndBelongsToUser_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(task1.getId()))
            .thenReturn(Optional.of(task1));

        // Act
        Task foundTask = taskService.getTaskById(task1.getId(), user.getId());

        // Assert
        assertNotNull(foundTask);
        assertEquals(task1.getId(), foundTask.getId());
        verify(taskRepository).findById(task1.getId());
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(999L))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () ->
            taskService.getTaskById(999L, user.getId())
        );
        verify(taskRepository).findById(999L);
    }

    @Test
    void getTaskById_WhenTaskBelongsToDifferentUser_ShouldThrowException() {
        // Arrange
        Task otherUserTask = new Task();
        otherUserTask.setId(3L);
        otherUserTask.setUserId(2L);

        when(taskRepository.findById(otherUserTask.getId()))
            .thenReturn(Optional.of(otherUserTask));

        // Act & Assert
        assertThrows(UnauthorizedTaskAccessException.class, () ->
            taskService.getTaskById(otherUserTask.getId(), user.getId())
        );
        verify(taskRepository).findById(otherUserTask.getId());
    }

    @Test
    void createTask_ShouldSetUserIdAndSaveTask() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");

        when(taskRepository.save(any(Task.class)))
            .thenAnswer(invocation -> {
                Task savedTask = invocation.getArgument(0);
                savedTask.setId(3L);
                return savedTask;
            });

        // Act
        Task createdTask = taskService.createTask(newTask, user.getId());

        // Assert
        assertNotNull(createdTask);
        assertEquals(user.getId(), createdTask.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExistsAndBelongsToUser_ShouldUpdateTask() {
        // Arrange
        when(taskRepository.findById(task1.getId()))
            .thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class)))
            .thenReturn(task1);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        // Act
        Task result = taskService.updateTask(task1.getId(), updatedTask, user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(task1.getId(), result.getId());
        verify(taskRepository).findById(task1.getId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_WhenTaskExistsAndBelongsToUser_ShouldUpdateStatus() {
        // Arrange
        when(taskRepository.findById(task1.getId()))
            .thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class)))
            .thenReturn(task1);

        // Act
        Task result = taskService.updateTaskStatus(task1.getId(), true, user.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isCompleted());
        verify(taskRepository).findById(task1.getId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExistsAndBelongsToUser_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.findById(task1.getId()))
            .thenReturn(Optional.of(task1));
        doNothing().when(taskRepository).deleteById(task1.getId());

        // Act
        taskService.deleteTask(task1.getId(), user.getId());

        // Assert
        verify(taskRepository).findById(task1.getId());
        verify(taskRepository).deleteById(task1.getId());
    }
} 