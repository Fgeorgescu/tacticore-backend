package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Kill {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("killer")
    private String killer;
    
    @JsonProperty("victim")
    private String victim;
    
    @JsonProperty("weapon")
    private String weapon;
    
    @JsonProperty("isGoodPlay")
    private boolean isGoodPlay;
    
    @JsonProperty("round")
    private int round;
    
    @JsonProperty("time")
    private String time;
    
    @JsonProperty("teamAlive")
    private TeamAlive teamAlive;
    
    @JsonProperty("position")
    private String position;
    
    // Inner class for team alive count
    public static class TeamAlive {
        @JsonProperty("ct")
        private int ct;
        
        @JsonProperty("t")
        private int t;
        
        public TeamAlive() {}
        
        public TeamAlive(int ct, int t) {
            this.ct = ct;
            this.t = t;
        }
        
        public int getCt() { return ct; }
        public void setCt(int ct) { this.ct = ct; }
        
        public int getT() { return t; }
        public void setT(int t) { this.t = t; }
    }
    
    // Constructors
    public Kill() {}
    
    public Kill(int id, String killer, String victim, String weapon, boolean isGoodPlay,
               int round, String time, TeamAlive teamAlive, String position) {
        this.id = id;
        this.killer = killer;
        this.victim = victim;
        this.weapon = weapon;
        this.isGoodPlay = isGoodPlay;
        this.round = round;
        this.time = time;
        this.teamAlive = teamAlive;
        this.position = position;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getKiller() { return killer; }
    public void setKiller(String killer) { this.killer = killer; }
    
    public String getVictim() { return victim; }
    public void setVictim(String victim) { this.victim = victim; }
    
    public String getWeapon() { return weapon; }
    public void setWeapon(String weapon) { this.weapon = weapon; }
    
    public boolean isGoodPlay() { return isGoodPlay; }
    public void setGoodPlay(boolean goodPlay) { isGoodPlay = goodPlay; }
    
    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public TeamAlive getTeamAlive() { return teamAlive; }
    public void setTeamAlive(TeamAlive teamAlive) { this.teamAlive = teamAlive; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
