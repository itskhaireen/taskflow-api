package com.example.taskmanager;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Entity // Tells Spring this is a database table
@Data // Lombok: auto-generated getters, setters, etc. Reduces boilerplate code.

public class Task {

    @Id // Primary Key
    @GeneratedValue // Auto-Increments ID
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long userId; // Link task to a user
    
}