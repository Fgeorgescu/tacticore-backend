package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class UserProfileDto {
    private String id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private UserStatsDto stats;
    private RecentActivityDto recentActivity;
    private UserPreferencesDto preferences;

    // Constructor
    public UserProfileDto(String id, String username, String email, String avatar, String role,
                         UserStatsDto stats, RecentActivityDto recentActivity, UserPreferencesDto preferences) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.role = role;
        this.stats = stats;
        this.recentActivity = recentActivity;
        this.preferences = preferences;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UserStatsDto getStats() { return stats; }
    public void setStats(UserStatsDto stats) { this.stats = stats; }

    public RecentActivityDto getRecentActivity() { return recentActivity; }
    public void setRecentActivity(RecentActivityDto recentActivity) { this.recentActivity = recentActivity; }

    public UserPreferencesDto getPreferences() { return preferences; }
    public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }

    // Nested DTOs
    public static class UserStatsDto {
        private int totalMatches;
        private int totalRounds;
        private int totalKills;
        private int totalDeaths;
        private int totalGoodPlays;
        private int totalBadPlays;
        private double averageScore;
        private double kdr;
        private double winRate;
        private String favoriteMap;
        private String favoriteWeapon;
        private double hoursPlayed;
        private LocalDateTime memberSince;

        public UserStatsDto(int totalMatches, int totalRounds, int totalKills, int totalDeaths,
                           int totalGoodPlays, int totalBadPlays, double averageScore, double kdr,
                           double winRate, String favoriteMap, String favoriteWeapon, 
                           double hoursPlayed, LocalDateTime memberSince) {
            this.totalMatches = totalMatches;
            this.totalRounds = totalRounds;
            this.totalKills = totalKills;
            this.totalDeaths = totalDeaths;
            this.totalGoodPlays = totalGoodPlays;
            this.totalBadPlays = totalBadPlays;
            this.averageScore = averageScore;
            this.kdr = kdr;
            this.winRate = winRate;
            this.favoriteMap = favoriteMap;
            this.favoriteWeapon = favoriteWeapon;
            this.hoursPlayed = hoursPlayed;
            this.memberSince = memberSince;
        }

        // Getters and Setters
        public int getTotalMatches() { return totalMatches; }
        public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }

        public int getTotalRounds() { return totalRounds; }
        public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }

        public int getTotalKills() { return totalKills; }
        public void setTotalKills(int totalKills) { this.totalKills = totalKills; }

        public int getTotalDeaths() { return totalDeaths; }
        public void setTotalDeaths(int totalDeaths) { this.totalDeaths = totalDeaths; }

        public int getTotalGoodPlays() { return totalGoodPlays; }
        public void setTotalGoodPlays(int totalGoodPlays) { this.totalGoodPlays = totalGoodPlays; }

        public int getTotalBadPlays() { return totalBadPlays; }
        public void setTotalBadPlays(int totalBadPlays) { this.totalBadPlays = totalBadPlays; }

        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

        public double getKdr() { return kdr; }
        public void setKdr(double kdr) { this.kdr = kdr; }

        public double getWinRate() { return winRate; }
        public void setWinRate(double winRate) { this.winRate = winRate; }

        public String getFavoriteMap() { return favoriteMap; }
        public void setFavoriteMap(String favoriteMap) { this.favoriteMap = favoriteMap; }

        public String getFavoriteWeapon() { return favoriteWeapon; }
        public void setFavoriteWeapon(String favoriteWeapon) { this.favoriteWeapon = favoriteWeapon; }

        public double getHoursPlayed() { return hoursPlayed; }
        public void setHoursPlayed(double hoursPlayed) { this.hoursPlayed = hoursPlayed; }

        public LocalDateTime getMemberSince() { return memberSince; }
        public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
    }

    public static class RecentActivityDto {
        private LocalDateTime lastMatchDate;
        private int matchesThisWeek;
        private int matchesThisMonth;

        public RecentActivityDto(LocalDateTime lastMatchDate, int matchesThisWeek, int matchesThisMonth) {
            this.lastMatchDate = lastMatchDate;
            this.matchesThisWeek = matchesThisWeek;
            this.matchesThisMonth = matchesThisMonth;
        }

        // Getters and Setters
        public LocalDateTime getLastMatchDate() { return lastMatchDate; }
        public void setLastMatchDate(LocalDateTime lastMatchDate) { this.lastMatchDate = lastMatchDate; }

        public int getMatchesThisWeek() { return matchesThisWeek; }
        public void setMatchesThisWeek(int matchesThisWeek) { this.matchesThisWeek = matchesThisWeek; }

        public int getMatchesThisMonth() { return matchesThisMonth; }
        public void setMatchesThisMonth(int matchesThisMonth) { this.matchesThisMonth = matchesThisMonth; }
    }

    public static class UserPreferencesDto {
        private String theme;
        private boolean notifications;

        public UserPreferencesDto(String theme, boolean notifications) {
            this.theme = theme;
            this.notifications = notifications;
        }

        // Getters and Setters
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }

        public boolean isNotifications() { return notifications; }
        public void setNotifications(boolean notifications) { this.notifications = notifications; }
    }
}
