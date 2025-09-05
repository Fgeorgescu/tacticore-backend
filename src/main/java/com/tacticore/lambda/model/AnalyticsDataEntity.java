package com.tacticore.lambda.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "analytics_data")
public class AnalyticsDataEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "kills", nullable = false)
    private Integer kills;
    
    @Column(name = "deaths", nullable = false)
    private Integer deaths;
    
    @Column(name = "kdr", nullable = false)
    private Double kdr;
    
    @Column(name = "score", nullable = false)
    private Double score;
    
    @Column(name = "good_plays", nullable = false)
    private Integer goodPlays;
    
    @Column(name = "bad_plays", nullable = false)
    private Integer badPlays;
    
    @Column(name = "matches", nullable = false)
    private Integer matches;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    // Constructors
    public AnalyticsDataEntity() {
        this.createdAt = java.time.LocalDateTime.now();
    }
    
    public AnalyticsDataEntity(LocalDate date, Integer kills, Integer deaths, Double kdr, 
                              Double score, Integer goodPlays, Integer badPlays, Integer matches) {
        this();
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Integer getKills() { return kills; }
    public void setKills(Integer kills) { this.kills = kills; }
    
    public Integer getDeaths() { return deaths; }
    public void setDeaths(Integer deaths) { this.deaths = deaths; }
    
    public Double getKdr() { return kdr; }
    public void setKdr(Double kdr) { this.kdr = kdr; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public Integer getGoodPlays() { return goodPlays; }
    public void setGoodPlays(Integer goodPlays) { this.goodPlays = goodPlays; }
    
    public Integer getBadPlays() { return badPlays; }
    public void setBadPlays(Integer badPlays) { this.badPlays = badPlays; }
    
    public Integer getMatches() { return matches; }
    public void setMatches(Integer matches) { this.matches = matches; }
    
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
