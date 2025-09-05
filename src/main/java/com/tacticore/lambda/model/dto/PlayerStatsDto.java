package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerStatsDto {
    
    @JsonProperty("player_name")
    private String playerName;
    
    @JsonProperty("kills")
    private Long kills;
    
    @JsonProperty("deaths")
    private Long deaths;
    
    @JsonProperty("kd_ratio")
    private Double kdRatio;
    
    @JsonProperty("headshots")
    private Long headshots;
    
    @JsonProperty("headshot_rate")
    private Double headshotRate;
    
    @JsonProperty("average_distance")
    private Double averageDistance;
    
    @JsonProperty("favorite_weapon")
    private String favoriteWeapon;
    
    @JsonProperty("performance_score")
    private Double performanceScore;
    
    // Constructors
    public PlayerStatsDto() {}
    
    public PlayerStatsDto(String playerName, Long kills, Long deaths, Double kdRatio, 
                         Long headshots, Double headshotRate, Double averageDistance, 
                         String favoriteWeapon, Double performanceScore) {
        this.playerName = playerName;
        this.kills = kills;
        this.deaths = deaths;
        this.kdRatio = kdRatio;
        this.headshots = headshots;
        this.headshotRate = headshotRate;
        this.averageDistance = averageDistance;
        this.favoriteWeapon = favoriteWeapon;
        this.performanceScore = performanceScore;
    }
    
    // Getters and Setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public Long getKills() { return kills; }
    public void setKills(Long kills) { this.kills = kills; }
    
    public Long getDeaths() { return deaths; }
    public void setDeaths(Long deaths) { this.deaths = deaths; }
    
    public Double getKdRatio() { return kdRatio; }
    public void setKdRatio(Double kdRatio) { this.kdRatio = kdRatio; }
    
    public Long getHeadshots() { return headshots; }
    public void setHeadshots(Long headshots) { this.headshots = headshots; }
    
    public Double getHeadshotRate() { return headshotRate; }
    public void setHeadshotRate(Double headshotRate) { this.headshotRate = headshotRate; }
    
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
    
    public String getFavoriteWeapon() { return favoriteWeapon; }
    public void setFavoriteWeapon(String favoriteWeapon) { this.favoriteWeapon = favoriteWeapon; }
    
    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
}
