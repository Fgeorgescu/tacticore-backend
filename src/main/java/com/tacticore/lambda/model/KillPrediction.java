package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Predicción individual de una kill por el modelo de IA
 */
public class KillPrediction {
    
    @JsonProperty("kill_id")
    private String killId;
    
    @JsonProperty("attacker")
    private String attacker;
    
    @JsonProperty("victim")
    private String victim;
    
    @JsonProperty("place")
    private String place;
    
    @JsonProperty("round")
    private Integer round;
    
    @JsonProperty("weapon")
    private String weapon;
    
    @JsonProperty("headshot")
    private Boolean headshot;
    
    @JsonProperty("distance")
    private Double distance;
    
    @JsonProperty("time_in_round")
    private Double timeInRound;
    
    @JsonProperty("context")
    private KillContext context;
    
    @JsonProperty("prediction")
    private PredictionResult prediction;
    
    // Constructores
    public KillPrediction() {}
    
    public KillPrediction(String killId, String attacker, String victim, String place, Integer round,
                         String weapon, Boolean headshot, Double distance, Double timeInRound,
                         KillContext context, PredictionResult prediction) {
        this.killId = killId;
        this.attacker = attacker;
        this.victim = victim;
        this.place = place;
        this.round = round;
        this.weapon = weapon;
        this.headshot = headshot;
        this.distance = distance;
        this.timeInRound = timeInRound;
        this.context = context;
        this.prediction = prediction;
    }
    
    // Getters y Setters
    public String getKillId() {
        return killId;
    }
    
    public void setKillId(String killId) {
        this.killId = killId;
    }
    
    public String getAttacker() {
        return attacker;
    }
    
    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }
    
    public String getVictim() {
        return victim;
    }
    
    public void setVictim(String victim) {
        this.victim = victim;
    }
    
    public String getPlace() {
        return place;
    }
    
    public void setPlace(String place) {
        this.place = place;
    }
    
    public Integer getRound() {
        return round;
    }
    
    public void setRound(Integer round) {
        this.round = round;
    }
    
    public String getWeapon() {
        return weapon;
    }
    
    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }
    
    public Boolean getHeadshot() {
        return headshot;
    }
    
    public void setHeadshot(Boolean headshot) {
        this.headshot = headshot;
    }
    
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public Double getTimeInRound() {
        return timeInRound;
    }
    
    public void setTimeInRound(Double timeInRound) {
        this.timeInRound = timeInRound;
    }
    
    public KillContext getContext() {
        return context;
    }
    
    public void setContext(KillContext context) {
        this.context = context;
    }
    
    public PredictionResult getPrediction() {
        return prediction;
    }
    
    public void setPrediction(PredictionResult prediction) {
        this.prediction = prediction;
    }
    
    @Override
    public String toString() {
        return "KillPrediction{" +
                "killId='" + killId + '\'' +
                ", attacker='" + attacker + '\'' +
                ", victim='" + victim + '\'' +
                ", place='" + place + '\'' +
                ", round=" + round +
                ", weapon='" + weapon + '\'' +
                ", headshot=" + headshot +
                ", distance=" + distance +
                ", timeInRound=" + timeInRound +
                ", context=" + context +
                ", prediction=" + prediction +
                '}';
    }
}
