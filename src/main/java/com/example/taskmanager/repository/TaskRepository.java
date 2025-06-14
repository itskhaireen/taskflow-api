package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdOrderById(Long userId);
    List<Task> findByUserIdAndCompletedOrderById(Long userId, boolean completed);
    List<Task> findByUserIdOrderByTitle(Long userId);
} 