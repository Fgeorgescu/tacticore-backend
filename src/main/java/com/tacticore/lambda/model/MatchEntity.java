package com.tacticore.lambda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class MatchEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_id", unique = true, nullable = false)
    private String matchId;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "map_name")
    private String mapName;
    
    @Column(name = "tickrate")
    private Integer tickrate;
    
    @Column(name = "total_kills")
    private Integer totalKills;
    
    @Column(name = "good_plays")
    private Integer goodPlays;
    
    @Column(name = "bad_plays")
    private Integer badPlays;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "has_video")
    private Boolean hasVideo;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public MatchEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MatchEntity(String matchId, String fileName, String mapName, Integer tickrate, 
                      Integer totalKills, String status, Boolean hasVideo) {
        this.matchId = matchId;
        this.fileName = fileName;
        this.mapName = mapName;
        this.tickrate = tickrate;
        this.totalKills = totalKills;
        this.status = status;
        this.hasVideo = hasVideo;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getMapName() { return mapName; }
    public void setMapName(String mapName) { this.mapName = mapName; }
    
    public Integer getTickrate() { return tickrate; }
    public void setTickrate(Integer tickrate) { this.tickrate = tickrate; }
    
    public Integer getTotalKills() { return totalKills; }
    public void setTotalKills(Integer totalKills) { this.totalKills = totalKills; }
    
    public Integer getGoodPlays() { return goodPlays; }
    public void setGoodPlays(Integer goodPlays) { this.goodPlays = goodPlays; }
    
    public Integer getBadPlays() { return badPlays; }
    public void setBadPlays(Integer badPlays) { this.badPlays = badPlays; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getHasVideo() { return hasVideo; }
    public void setHasVideo(Boolean hasVideo) { this.hasVideo = hasVideo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
