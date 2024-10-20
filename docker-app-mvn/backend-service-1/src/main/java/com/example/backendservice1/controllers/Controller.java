package com.example.backendservice1.controllers;

import com.example.backendservice1.entities.User;
import com.example.backendservice1.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private UserService userService;
    
    @GetMapping("/data")
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Hello from the backend!");
        return data;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        try {
            logger.info("Attempting to log in user: {}", username);
            logger.info("Attempting to log in password: {}", password);
            User user = userService.authenticate(username, password);
            if (user != null) {
                logger.info("Login successful for user: {}", username);
                return ResponseEntity.ok("Login successful!");
            } else {
                logger.warn("Invalid credentials for user: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Error during login for user: {}. Exception: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }
}