package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class RoundAnalysisDto {
    
    @JsonProperty("round_number")
    private Integer roundNumber;
    
    @JsonProperty("total_kills")
    private Long totalKills;
    
    @JsonProperty("duration")
    private Double duration;
    
    @JsonProperty("most_active_player")
    private String mostActivePlayer;
    
    @JsonProperty("hot_spots")
    private List<Map<String, Object>> hotSpots;
    
    @JsonProperty("weapon_distribution")
    private Map<String, Long> weaponDistribution;
    
    @JsonProperty("ct_t_balance")
    private Map<String, Long> ctTBalance;
    
    @JsonProperty("headshot_rate")
    private Double headshotRate;
    
    @JsonProperty("average_distance")
    private Double averageDistance;
    
    // Constructors
    public RoundAnalysisDto() {}
    
    public RoundAnalysisDto(Integer roundNumber, Long totalKills, Double duration, 
                           String mostActivePlayer, List<Map<String, Object>> hotSpots,
                           Map<String, Long> weaponDistribution, Map<String, Long> ctTBalance,
                           Double headshotRate, Double averageDistance) {
        this.roundNumber = roundNumber;
        this.totalKills = totalKills;
        this.duration = duration;
        this.mostActivePlayer = mostActivePlayer;
        this.hotSpots = hotSpots;
        this.weaponDistribution = weaponDistribution;
        this.ctTBalance = ctTBalance;
        this.headshotRate = headshotRate;
        this.averageDistance = averageDistance;
    }
    
    // Getters and Setters
    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }
    
    public Long getTotalKills() { return totalKills; }
    public void setTotalKills(Long totalKills) { this.totalKills = totalKills; }
    
    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }
    
    public String getMostActivePlayer() { return mostActivePlayer; }
    public void setMostActivePlayer(String mostActivePlayer) { this.mostActivePlayer = mostActivePlayer; }
    
    public List<Map<String, Object>> getHotSpots() { return hotSpots; }
    public void setHotSpots(List<Map<String, Object>> hotSpots) { this.hotSpots = hotSpots; }
    
    public Map<String, Long> getWeaponDistribution() { return weaponDistribution; }
    public void setWeaponDistribution(Map<String, Long> weaponDistribution) { this.weaponDistribution = weaponDistribution; }
    
    public Map<String, Long> getCtTBalance() { return ctTBalance; }
    public void setCtTBalance(Map<String, Long> ctTBalance) { this.ctTBalance = ctTBalance; }
    
    public Double getHeadshotRate() { return headshotRate; }
    public void setHeadshotRate(Double headshotRate) { this.headshotRate = headshotRate; }
    
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
}
