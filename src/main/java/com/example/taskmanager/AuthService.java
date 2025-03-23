package com.example.taskmanager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Key signingKey;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;

        final String SECRET_KEY = "X7kP9mL2qW4vZ8jR5tY6uI3oP1nM8xQ2wE4rT9yU0sA6hJ3v";
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.passwordEncoder = new BCryptPasswordEncoder(); // Add Encoder
    }

    public void register(String username, String password){
        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username Already Exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash Password
        userRepository.save(user);
    }

    public String login(String username, String password){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        String token = generateToken(username);
        return token;
    }

    private String generateToken(String username){
            return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expire In 1 Hour
                .signWith(signingKey) // New way: Use the Key Object
                .compact();
        
    }
    
}