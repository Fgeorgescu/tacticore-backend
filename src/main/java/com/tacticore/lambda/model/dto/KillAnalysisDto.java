package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class KillAnalysisDto {
    
    @JsonProperty("total_kills")
    private Long totalKills;
    
    @JsonProperty("total_headshots")
    private Long totalHeadshots;
    
    @JsonProperty("headshot_rate")
    private Double headshotRate;
    
    @JsonProperty("average_distance")
    private Double averageDistance;
    
    @JsonProperty("average_time_in_round")
    private Double averageTimeInRound;
    
    @JsonProperty("weapon_stats")
    private List<Map<String, Object>> weaponStats;
    
    @JsonProperty("location_stats")
    private List<Map<String, Object>> locationStats;
    
    @JsonProperty("round_stats")
    private List<Map<String, Object>> roundStats;
    
    @JsonProperty("side_stats")
    private List<Map<String, Object>> sideStats;
    
    @JsonProperty("top_players")
    private List<Map<String, Object>> topPlayers;
    
    @JsonProperty("prediction_stats")
    private List<Map<String, Object>> predictionStats;
    
    // Constructors
    public KillAnalysisDto() {}
    
    public KillAnalysisDto(Long totalKills, Long totalHeadshots, Double headshotRate,
                          Double averageDistance, Double averageTimeInRound,
                          List<Map<String, Object>> weaponStats,
                          List<Map<String, Object>> locationStats,
                          List<Map<String, Object>> roundStats,
                          List<Map<String, Object>> sideStats,
                          List<Map<String, Object>> topPlayers,
                          List<Map<String, Object>> predictionStats) {
        this.totalKills = totalKills;
        this.totalHeadshots = totalHeadshots;
        this.headshotRate = headshotRate;
        this.averageDistance = averageDistance;
        this.averageTimeInRound = averageTimeInRound;
        this.weaponStats = weaponStats;
        this.locationStats = locationStats;
        this.roundStats = roundStats;
        this.sideStats = sideStats;
        this.topPlayers = topPlayers;
        this.predictionStats = predictionStats;
    }
    
    // Getters and Setters
    public Long getTotalKills() { return totalKills; }
    public void setTotalKills(Long totalKills) { this.totalKills = totalKills; }
    
    public Long getTotalHeadshots() { return totalHeadshots; }
    public void setTotalHeadshots(Long totalHeadshots) { this.totalHeadshots = totalHeadshots; }
    
    public Double getHeadshotRate() { return headshotRate; }
    public void setHeadshotRate(Double headshotRate) { this.headshotRate = headshotRate; }
    
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
    
    public Double getAverageTimeInRound() { return averageTimeInRound; }
    public void setAverageTimeInRound(Double averageTimeInRound) { this.averageTimeInRound = averageTimeInRound; }
    
    public List<Map<String, Object>> getWeaponStats() { return weaponStats; }
    public void setWeaponStats(List<Map<String, Object>> weaponStats) { this.weaponStats = weaponStats; }
    
    public List<Map<String, Object>> getLocationStats() { return locationStats; }
    public void setLocationStats(List<Map<String, Object>> locationStats) { this.locationStats = locationStats; }
    
    public List<Map<String, Object>> getRoundStats() { return roundStats; }
    public void setRoundStats(List<Map<String, Object>> roundStats) { this.roundStats = roundStats; }
    
    public List<Map<String, Object>> getSideStats() { return sideStats; }
    public void setSideStats(List<Map<String, Object>> sideStats) { this.sideStats = sideStats; }
    
    public List<Map<String, Object>> getTopPlayers() { return topPlayers; }
    public void setTopPlayers(List<Map<String, Object>> topPlayers) { this.topPlayers = topPlayers; }
    
    public List<Map<String, Object>> getPredictionStats() { return predictionStats; }
    public void setPredictionStats(List<Map<String, Object>> predictionStats) { this.predictionStats = predictionStats; }
}
