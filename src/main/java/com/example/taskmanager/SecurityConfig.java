package com.example.taskmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String secretKey = "X7kP9mL2qW4vZ8jR5tY6uI3oP1nM8xQ2wE4rT9yU0sA6hJ3v"; // Same as AuthService

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtFilter jwtFilter = new JwtFilter(secretKey);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Allow Login Endpoints
                .requestMatchers("/h2-console/**").permitAll() // H2 Console Open
                .requestMatchers("/api/tasks/**").authenticated() // Tasks Required Token
                .anyRequest().authenticated() // Any other Endpoints need Authentication
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(httpSecurityHeadersConfigurer -> {
                httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
             });
        return http.build();
    }
}