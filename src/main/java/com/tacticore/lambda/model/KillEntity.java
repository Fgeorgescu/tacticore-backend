package com.tacticore.lambda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kills")
public class KillEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kill_id", unique = true, nullable = false)
    private String killId;
    
    @Column(name = "match_id", nullable = false)
    private String matchId;
    
    @Column(name = "attacker", nullable = false)
    private String attacker;
    
    @Column(name = "victim", nullable = false)
    private String victim;
    
    @Column(name = "place")
    private String place;
    
    @Column(name = "round_number", nullable = false)
    private Integer round;
    
    @Column(name = "weapon", nullable = false)
    private String weapon;
    
    @Column(name = "headshot", nullable = false)
    private Boolean headshot;
    
    @Column(name = "distance")
    private Double distance;
    
    @Column(name = "time_in_round")
    private Double timeInRound;
    
    @Column(name = "kill_tick")
    private Long killTick;
    
    @Column(name = "side")
    private String side;
    
    @Column(name = "attacker_x")
    private Double attackerX;
    
    @Column(name = "attacker_y")
    private Double attackerY;
    
    @Column(name = "attacker_z")
    private Double attackerZ;
    
    @Column(name = "victim_x")
    private Double victimX;
    
    @Column(name = "victim_y")
    private Double victimY;
    
    @Column(name = "victim_z")
    private Double victimZ;
    
    @Column(name = "attacker_health")
    private Double attackerHealth;
    
    @Column(name = "victim_health")
    private Double victimHealth;
    
    @Column(name = "flash_near")
    private Boolean flashNear;
    
    @Column(name = "smoke_near")
    private Boolean smokeNear;
    
    @Column(name = "molotov_near")
    private Boolean molotovNear;
    
    @Column(name = "he_near")
    private Boolean heNear;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public KillEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKillId() { return killId; }
    public void setKillId(String killId) { this.killId = killId; }
    
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    
    public String getAttacker() { return attacker; }
    public void setAttacker(String attacker) { this.attacker = attacker; }
    
    public String getVictim() { return victim; }
    public void setVictim(String victim) { this.victim = victim; }
    
    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }
    
    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }
    
    public String getWeapon() { return weapon; }
    public void setWeapon(String weapon) { this.weapon = weapon; }
    
    public Boolean getHeadshot() { return headshot; }
    public void setHeadshot(Boolean headshot) { this.headshot = headshot; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Double getTimeInRound() { return timeInRound; }
    public void setTimeInRound(Double timeInRound) { this.timeInRound = timeInRound; }
    
    public Long getKillTick() { return killTick; }
    public void setKillTick(Long killTick) { this.killTick = killTick; }
    
    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }
    
    public Double getAttackerX() { return attackerX; }
    public void setAttackerX(Double attackerX) { this.attackerX = attackerX; }
    
    public Double getAttackerY() { return attackerY; }
    public void setAttackerY(Double attackerY) { this.attackerY = attackerY; }
    
    public Double getAttackerZ() { return attackerZ; }
    public void setAttackerZ(Double attackerZ) { this.attackerZ = attackerZ; }
    
    public Double getVictimX() { return victimX; }
    public void setVictimX(Double victimX) { this.victimX = victimX; }
    
    public Double getVictimY() { return victimY; }
    public void setVictimY(Double victimY) { this.victimY = victimY; }
    
    public Double getVictimZ() { return victimZ; }
    public void setVictimZ(Double victimZ) { this.victimZ = victimZ; }
    
    public Double getAttackerHealth() { return attackerHealth; }
    public void setAttackerHealth(Double attackerHealth) { this.attackerHealth = attackerHealth; }
    
    public Double getVictimHealth() { return victimHealth; }
    public void setVictimHealth(Double victimHealth) { this.victimHealth = victimHealth; }
    
    public Boolean getFlashNear() { return flashNear; }
    public void setFlashNear(Boolean flashNear) { this.flashNear = flashNear; }
    
    public Boolean getSmokeNear() { return smokeNear; }
    public void setSmokeNear(Boolean smokeNear) { this.smokeNear = smokeNear; }
    
    public Boolean getMolotovNear() { return molotovNear; }
    public void setMolotovNear(Boolean molotovNear) { this.molotovNear = molotovNear; }
    
    public Boolean getHeNear() { return heNear; }
    public void setHeNear(Boolean heNear) { this.heNear = heNear; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
