package com.tacticore.lambda.service;

import com.tacticore.lambda.model.UserEntity;
import com.tacticore.lambda.model.UserRole;
import com.tacticore.lambda.model.dto.UserDto;
import com.tacticore.lambda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Create or get user
    public UserEntity createOrGetUser(String name, String role) {
        Optional<UserEntity> existingUser = userRepository.findByName(name);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        UserEntity newUser = new UserEntity(name, role);
        return userRepository.save(newUser);
    }
    
    // Create or get user with default role
    public UserEntity createOrGetUser(String name) {
        return createOrGetUser(name, UserRole.SUPPORT.getDisplayName());
    }
    
    // Get user by name
    public Optional<UserEntity> getUserByName(String name) {
        return userRepository.findByName(name);
    }
    
    // Check if user exists
    public boolean userExists(String name) {
        return userRepository.existsByName(name);
    }
    
    // Get all users
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get users by role
    public List<UserEntity> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    // Search users by name
    public List<UserEntity> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }
    
    // Get top players by average score
    public List<UserEntity> getTopPlayersByScore() {
        return userRepository.findAllOrderByAverageScoreDesc();
    }
    
    // Get top players by kills
    public List<UserEntity> getTopPlayersByKills() {
        return userRepository.findAllOrderByTotalKillsDesc();
    }
    
    // Get top players by KDR
    public List<UserEntity> getTopPlayersByKDR() {
        return userRepository.findAllOrderByKDRDesc();
    }
    
    // Get top players with minimum matches
    public List<UserEntity> getTopPlayersByMatches(int minMatches) {
        return userRepository.findTopPlayersByMatches(minMatches);
    }
    
    // Update user stats after a match
    public void updateUserStats(String userName, int kills, int deaths, double score) {
        Optional<UserEntity> userOpt = userRepository.findByName(userName);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.updateStats(kills, deaths, score);
            userRepository.save(user);
        }
    }
    
    // Update multiple users stats
    public void updateMultipleUsersStats(List<String> userNames, List<Integer> kills, List<Integer> deaths, List<Double> scores) {
        for (int i = 0; i < userNames.size(); i++) {
            if (i < kills.size() && i < deaths.size() && i < scores.size()) {
                updateUserStats(userNames.get(i), kills.get(i), deaths.get(i), scores.get(i));
            }
        }
    }
    
    // Ensure users exist (create if they don't)
    public List<UserEntity> ensureUsersExist(List<String> userNames) {
        return userNames.stream()
                .map(this::createOrGetUser)
                .collect(Collectors.toList());
    }
    
    // Get user statistics
    public UserStats getUserStatistics() {
        Long totalUsers = userRepository.countAllUsers();
        Double averageScore = userRepository.getAverageScoreOfAllUsers();
        Long totalKills = userRepository.getTotalKillsOfAllUsers();
        Long totalDeaths = userRepository.getTotalDeathsOfAllUsers();
        Long totalMatches = userRepository.getTotalMatchesOfAllUsers();
        List<Object[]> roleStats = userRepository.getUsersCountByRole();
        
        return new UserStats(
            totalUsers != null ? totalUsers : 0L,
            averageScore != null ? averageScore : 0.0,
            totalKills != null ? totalKills : 0L,
            totalDeaths != null ? totalDeaths : 0L,
            totalMatches != null ? totalMatches : 0L,
            roleStats
        );
    }
    
    // Convert Entity to DTO
    public UserDto convertToDto(UserEntity entity) {
        return new UserDto(
            entity.getId(),
            entity.getName(),
            entity.getRole(),
            entity.getAverageScore(),
            entity.getTotalKills(),
            entity.getTotalDeaths(),
            entity.getTotalMatches(),
            entity.getKDR()
        );
    }
    
    // Convert list of entities to DTOs
    public List<UserDto> convertToDtoList(List<UserEntity> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get available roles
    public String[] getAvailableRoles() {
        return UserRole.getAllDisplayNames();
    }
    
    // Inner class for user statistics
    public static class UserStats {
        private final Long totalUsers;
        private final Double averageScore;
        private final Long totalKills;
        private final Long totalDeaths;
        private final Long totalMatches;
        private final List<Object[]> roleStats;
        
        public UserStats(Long totalUsers, Double averageScore, Long totalKills, 
                        Long totalDeaths, Long totalMatches, List<Object[]> roleStats) {
            this.totalUsers = totalUsers;
            this.averageScore = averageScore;
            this.totalKills = totalKills;
            this.totalDeaths = totalDeaths;
            this.totalMatches = totalMatches;
            this.roleStats = roleStats;
        }
        
        // Getters
        public Long getTotalUsers() { return totalUsers; }
        public Double getAverageScore() { return averageScore; }
        public Long getTotalKills() { return totalKills; }
        public Long getTotalDeaths() { return totalDeaths; }
        public Long getTotalMatches() { return totalMatches; }
        public List<Object[]> getRoleStats() { return roleStats; }
    }
}
