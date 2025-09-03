package com.tesis.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardStats {
    
    @JsonProperty("totalMatches")
    private int totalMatches;
    
    @JsonProperty("totalKills")
    private int totalKills;
    
    @JsonProperty("totalDeaths")
    private int totalDeaths;
    
    @JsonProperty("totalGoodPlays")
    private int totalGoodPlays;
    
    @JsonProperty("totalBadPlays")
    private int totalBadPlays;
    
    @JsonProperty("averageScore")
    private double averageScore;
    
    @JsonProperty("kdr")
    private double kdr;
    
    // Constructors
    public DashboardStats() {}
    
    public DashboardStats(int totalMatches, int totalKills, int totalDeaths, 
                        int totalGoodPlays, int totalBadPlays, double averageScore, double kdr) {
        this.totalMatches = totalMatches;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalGoodPlays = totalGoodPlays;
        this.totalBadPlays = totalBadPlays;
        this.averageScore = averageScore;
        this.kdr = kdr;
    }
    
    // Getters and Setters
    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }
    
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
}
