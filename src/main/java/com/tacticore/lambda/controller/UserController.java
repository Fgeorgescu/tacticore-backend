package com.tacticore.lambda.controller;

import com.tacticore.lambda.model.UserEntity;
import com.tacticore.lambda.model.dto.UserDto;
import com.tacticore.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get user by name
    @GetMapping("/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String name) {
        Optional<UserEntity> user = userService.getUserByName(name);
        if (user.isPresent()) {
            UserDto userDto = userService.convertToDto(user.get());
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Check if user exists
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> userExists(@PathVariable String name) {
        boolean exists = userService.userExists(name);
        return ResponseEntity.ok(exists);
    }
    
    // Search users by name
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        List<UserEntity> users = userService.searchUsersByName(name);
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        List<UserEntity> users = userService.getUsersByRole(role);
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get top players by average score
    @GetMapping("/top/score")
    public ResponseEntity<List<UserDto>> getTopPlayersByScore() {
        List<UserEntity> users = userService.getTopPlayersByScore();
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get top players by kills
    @GetMapping("/top/kills")
    public ResponseEntity<List<UserDto>> getTopPlayersByKills() {
        List<UserEntity> users = userService.getTopPlayersByKills();
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get top players by KDR
    @GetMapping("/top/kdr")
    public ResponseEntity<List<UserDto>> getTopPlayersByKDR() {
        List<UserEntity> users = userService.getTopPlayersByKDR();
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Get top players with minimum matches
    @GetMapping("/top/matches")
    public ResponseEntity<List<UserDto>> getTopPlayersByMatches(@RequestParam(defaultValue = "5") int minMatches) {
        List<UserEntity> users = userService.getTopPlayersByMatches(minMatches);
        List<UserDto> userDtos = userService.convertToDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
    
    // Create or get user
    @PostMapping
    public ResponseEntity<UserDto> createOrGetUser(@RequestBody CreateUserRequest request) {
        UserEntity user = userService.createOrGetUser(request.getName(), request.getRole());
        UserDto userDto = userService.convertToDto(user);
        return ResponseEntity.ok(userDto);
    }
    
    // Get available roles
    @GetMapping("/roles")
    public ResponseEntity<String[]> getAvailableRoles() {
        String[] roles = userService.getAvailableRoles();
        return ResponseEntity.ok(roles);
    }
    
    // Get user statistics
    @GetMapping("/stats")
    public ResponseEntity<UserService.UserStats> getUserStatistics() {
        UserService.UserStats stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
    
    // Inner class for request body
    public static class CreateUserRequest {
        private String name;
        private String role;
        
        public CreateUserRequest() {}
        
        public CreateUserRequest(String name, String role) {
            this.name = name;
            this.role = role;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}