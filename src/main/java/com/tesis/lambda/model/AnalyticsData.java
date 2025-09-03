package com.tesis.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class AnalyticsData {
    
    @JsonProperty("date")
    private LocalDate date;
    
    @JsonProperty("kills")
    private int kills;
    
    @JsonProperty("deaths")
    private int deaths;
    
    @JsonProperty("kdr")
    private double kdr;
    
    @JsonProperty("score")
    private double score;
    
    @JsonProperty("goodPlays")
    private int goodPlays;
    
    @JsonProperty("badPlays")
    private int badPlays;
    
    @JsonProperty("matches")
    private int matches;
    
    // Constructors
    public AnalyticsData() {}
    
    public AnalyticsData(LocalDate date, int kills, int deaths, double kdr, double score,
                        int goodPlays, int badPlays, int matches) {
        this.date = date;
        this.kills = kills;
        this.deaths = deaths;
        this.kdr = kdr;
        this.score = score;
        this.goodPlays = goodPlays;
        this.badPlays = badPlays;
        this.matches = matches;
    }
    
    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    
    public double getKdr() { return kdr; }
    public void setKdr(double kdr) { this.kdr = kdr; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public int getGoodPlays() { return goodPlays; }
    public void setGoodPlays(int goodPlays) { this.goodPlays = goodPlays; }
    
    public int getBadPlays() { return badPlays; }
    public void setBadPlays(int badPlays) { this.badPlays = badPlays; }
    
    public int getMatches() { return matches; }
    public void setMatches(int matches) { this.matches = matches; }
}
