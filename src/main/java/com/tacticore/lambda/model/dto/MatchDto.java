package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class MatchDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("hasVideo")
    private boolean hasVideo;
    
    @JsonProperty("map")
    private String map;
    
    @JsonProperty("gameType")
    private String gameType;
    
    @JsonProperty("kills")
    private int kills;
    
    @JsonProperty("deaths")
    private int deaths;
    
    @JsonProperty("goodPlays")
    private int goodPlays;
    
    @JsonProperty("badPlays")
    private int badPlays;
    
    @JsonProperty("duration")
    private String duration;
    
    @JsonProperty("score")
    private double score;
    
    @JsonProperty("date")
    private LocalDateTime date;
    
    @JsonProperty("status")
    private String status;
    
    // Constructors
    public MatchDto() {}
    
    public MatchDto(String id, String fileName, boolean hasVideo, String map, String gameType,
                   int kills, int deaths, int goodPlays, int badPlays, String duration, 
                   double score, LocalDateTime date) {
        this.id = id;
        this.fileName = fileName;
        this.hasVideo = hasVideo;
        this.map = map;
        this.gameType = gameType;
        this.kills = kills;
        this.deaths = deaths;
        this.goodPlays = goodPlays;
        this.badPlays = badPlays;
        this.duration = duration;
        this.score = score;
        this.date = date;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public boolean isHasVideo() { return hasVideo; }
    public void setHasVideo(boolean hasVideo) { this.hasVideo = hasVideo; }
    
    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }
    
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    
    public int getGoodPlays() { return goodPlays; }
    public void setGoodPlays(int goodPlays) { this.goodPlays = goodPlays; }
    
    public int getBadPlays() { return badPlays; }
    public void setBadPlays(int badPlays) { this.badPlays = badPlays; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
