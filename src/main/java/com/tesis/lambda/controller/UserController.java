package com.tesis.lambda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    // Mock user data
    private final Map<String, Object> mockUser = new HashMap<>();
    
    public UserController() {
        mockUser.put("id", "user_123");
        mockUser.put("username", "tacticore_player");
        mockUser.put("email", "player@tacticore.com");
        mockUser.put("createdAt", LocalDateTime.of(2024, 1, 1, 0, 0));
    }
    
    // GET /api/user/profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        return ResponseEntity.ok(mockUser);
    }
    
    // PUT /api/user/profile
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        
        if (username != null) {
            mockUser.put("username", username);
        }
        
        if (email != null) {
            mockUser.put("email", email);
        }
        
        mockUser.put("updatedAt", LocalDateTime.now());
        
        return ResponseEntity.ok(mockUser);
    }
}
