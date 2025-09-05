package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Respuesta del modelo de IA para análisis de kills
 */
public class AIModelResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("total_kills")
    private Integer totalKills;
    
    @JsonProperty("map")
    private String map;
    
    @JsonProperty("tickrate")
    private Integer tickrate;
    
    @JsonProperty("predictions")
    private List<KillPrediction> predictions;
    
    // Constructores
    public AIModelResponse() {}
    
    public AIModelResponse(String status, Integer totalKills, String map, Integer tickrate, List<KillPrediction> predictions) {
        this.status = status;
        this.totalKills = totalKills;
        this.map = map;
        this.tickrate = tickrate;
        this.predictions = predictions;
    }
    
    // Getters y Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getTotalKills() {
        return totalKills;
    }
    
    public void setTotalKills(Integer totalKills) {
        this.totalKills = totalKills;
    }
    
    public String getMap() {
        return map;
    }
    
    public void setMap(String map) {
        this.map = map;
    }
    
    public Integer getTickrate() {
        return tickrate;
    }
    
    public void setTickrate(Integer tickrate) {
        this.tickrate = tickrate;
    }
    
    public List<KillPrediction> getPredictions() {
        return predictions;
    }
    
    public void setPredictions(List<KillPrediction> predictions) {
        this.predictions = predictions;
    }
    
    @Override
    public String toString() {
        return "AIModelResponse{" +
                "status='" + status + '\'' +
                ", totalKills=" + totalKills +
                ", map='" + map + '\'' +
                ", tickrate=" + tickrate +
                ", predictions=" + predictions +
                '}';
    }
}
