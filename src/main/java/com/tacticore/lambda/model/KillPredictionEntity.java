package com.tacticore.lambda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kill_predictions")
public class KillPredictionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kill_id", nullable = false)
    private String killId;
    
    @Column(name = "label", nullable = false)
    private String label;
    
    @Column(name = "confidence", nullable = false)
    private Double confidence;
    
    @Column(name = "is_top_prediction", nullable = false)
    private Boolean isTopPrediction;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public KillPredictionEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    public KillPredictionEntity(String killId, String label, Double confidence, Boolean isTopPrediction) {
        this.killId = killId;
        this.label = label;
        this.confidence = confidence;
        this.isTopPrediction = isTopPrediction;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKillId() { return killId; }
    public void setKillId(String killId) { this.killId = killId; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public Boolean getIsTopPrediction() { return isTopPrediction; }
    public void setIsTopPrediction(Boolean isTopPrediction) { this.isTopPrediction = isTopPrediction; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
