package com.example.taskmanager; // A repository is how Spring talks to the database

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {

    // JPA gives basic CRUD for free!
    // JpaRepository<Task, Long> handles Task objects with Long as the ID type.
    // Auto provides methods : save(), findAll
 
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndCompleted(Long userId, boolean completed);
    List<Task> findByUserIdOrderByTitle(Long userId);
    List<Task> findByUserIdOrderById(Long userId);
}