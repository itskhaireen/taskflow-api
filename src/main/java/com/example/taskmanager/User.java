package com.example.taskmanager;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;

@Entity
@Table(name = "app_user")
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password; // --- hash later
    
}