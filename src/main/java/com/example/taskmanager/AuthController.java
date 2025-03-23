package com.example.taskmanager;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }   

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            JwtFilter.blacklistToken(token); // Add To Blacklist
        }
        SecurityContextHolder.clearContext();
        return "Logged Out Successfully";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request.username(), request.password());
        return "User Registered Successfully";
    }

}

record LoginRequest(String username, String password) {}
record RegisterRequest(String username, String password) {}