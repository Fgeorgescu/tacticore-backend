package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Contexto detallado de una kill para el análisis del modelo de IA
 */
public class KillContext {
    
    @JsonProperty("kill_tick")
    private Integer killTick;
    
    @JsonProperty("attacker_name")
    private String attackerName;
    
    @JsonProperty("victim_name")
    private String victimName;
    
    @JsonProperty("side")
    private String side;
    
    @JsonProperty("place")
    private String place;
    
    @JsonProperty("headshot")
    private Boolean headshot;
    
    @JsonProperty("attacker_weapon")
    private String attackerWeapon;
    
    @JsonProperty("victim_weapon")
    private String victimWeapon;
    
    @JsonProperty("attacker_x")
    private Double attackerX;
    
    @JsonProperty("attacker_y")
    private Double attackerY;
    
    @JsonProperty("attacker_z")
    private Double attackerZ;
    
    @JsonProperty("attacker_health")
    private Double attackerHealth;
    
    @JsonProperty("attacker_vel_x")
    private Integer attackerVelX;
    
    @JsonProperty("attacker_vel_y")
    private Integer attackerVelY;
    
    @JsonProperty("victim_x")
    private Double victimX;
    
    @JsonProperty("victim_y")
    private Double victimY;
    
    @JsonProperty("victim_z")
    private Double victimZ;
    
    @JsonProperty("victim_health")
    private Double victimHealth;
    
    @JsonProperty("time_in_round_s")
    private Double timeInRoundS;
    
    @JsonProperty("distance_xy")
    private Double distanceXy;
    
    @JsonProperty("approach_align_deg")
    private Double approachAlignDeg;
    
    @JsonProperty("flash_near")
    private Boolean flashNear;
    
    @JsonProperty("smoke_near")
    private Boolean smokeNear;
    
    @JsonProperty("molotov_near")
    private Boolean molotovNear;
    
    @JsonProperty("he_near")
    private Boolean heNear;
    
    @JsonProperty("attacker_image_x")
    private Double attackerImageX;
    
    @JsonProperty("attacker_image_y")
    private Double attackerImageY;
    
    @JsonProperty("victim_image_x")
    private Double victimImageX;
    
    @JsonProperty("victim_image_y")
    private Double victimImageY;
    
    @JsonProperty("debug_attacker")
    private DebugInfo debugAttacker;
    
    @JsonProperty("debug_victim")
    private DebugInfo debugVictim;
    
    @JsonProperty("had_sound_cue")
    private Boolean hadSoundCue;
    
    @JsonProperty("sound_cue_types")
    private List<String> soundCueTypes;
    
    @JsonProperty("sound_cue_count")
    private Integer soundCueCount;
    
    @JsonProperty("last_sound_tick")
    private Integer lastSoundTick;
    
    @JsonProperty("time_since_last_sound")
    private Double timeSinceLastSound;
    
    @JsonProperty("attacker_visible")
    private Boolean attackerVisible;
    
    @JsonProperty("attacker_distance_when_heard")
    private Double attackerDistanceWhenHeard;
    
    @JsonProperty("victim_was_aware")
    private Boolean victimWasAware;
    
    @JsonProperty("victim_was_watching")
    private Boolean victimWasWatching;
    
    @JsonProperty("victim_was_backstabbed")
    private Boolean victimWasBackstabbed;
    
    @JsonProperty("time_since_last_sight")
    private Double timeSinceLastSight;
    
    @JsonProperty("awareness_confidence")
    private Double awarenessConfidence;
    
    @JsonProperty("angle_to_attacker")
    private Double angleToAttacker;
    
    @JsonProperty("victim_view_angle")
    private Integer victimViewAngle;
    
    @JsonProperty("angle_difference")
    private Double angleDifference;
    
    @JsonProperty("awareness_detected_at_tick")
    private Integer awarenessDetectedAtTick;
    
    @JsonProperty("round_number")
    private Integer roundNumber;
    
    @JsonProperty("round_phase")
    private String roundPhase;
    
    @JsonProperty("bomb_planted")
    private Boolean bombPlanted;
    
    @JsonProperty("time_since_bomb_plant")
    private Double timeSinceBombPlant;
    
    @JsonProperty("time_until_bomb_explode")
    private Double timeUntilBombExplode;
    
    @JsonProperty("players_alive_t")
    private Integer playersAliveT;
    
    @JsonProperty("players_alive_ct")
    private Integer playersAliveCt;
    
    @JsonProperty("round_win_probability")
    private Double roundWinProbability;
    
    @JsonProperty("match_score_t")
    private Integer matchScoreT;
    
    @JsonProperty("match_score_ct")
    private Integer matchScoreCt;
    
    @JsonProperty("team1_name")
    private String team1Name;
    
    @JsonProperty("team2_name")
    private String team2Name;
    
    @JsonProperty("game_id")
    private String gameId;
    
    @JsonProperty("flash_active")
    private Boolean flashActive;
    
    @JsonProperty("smoke_active")
    private Boolean smokeActive;
    
    @JsonProperty("molotov_active")
    private Boolean molotovActive;
    
    @JsonProperty("he_active")
    private Boolean heActive;
    
    @JsonProperty("utility_count")
    private Integer utilityCount;
    
    @JsonProperty("closest_utility_distance")
    private Double closestUtilityDistance;
    
    @JsonProperty("utility_thrower")
    private String utilityThrower;
    
    @JsonProperty("time_since_utility")
    private Double timeSinceUtility;
    
    @JsonProperty("utility_affecting_kill")
    private Boolean utilityAffectingKill;
    
    @JsonProperty("attacker_is_alive")
    private Boolean attackerIsAlive;
    
    @JsonProperty("attacker_is_ducking")
    private Boolean attackerIsDucking;
    
    @JsonProperty("attacker_is_scoped")
    private Boolean attackerIsScoped;
    
    @JsonProperty("attacker_is_moving")
    private Boolean attackerIsMoving;
    
    @JsonProperty("attacker_movement_speed")
    private Integer attackerMovementSpeed;
    
    @JsonProperty("attacker_has_primary")
    private Boolean attackerHasPrimary;
    
    @JsonProperty("attacker_has_secondary")
    private Boolean attackerHasSecondary;
    
    @JsonProperty("attacker_has_utility")
    private Boolean attackerHasUtility;
    
    @JsonProperty("victim_is_alive")
    private Boolean victimIsAlive;
    
    @JsonProperty("victim_is_ducking")
    private Boolean victimIsDucking;
    
    @JsonProperty("victim_is_scoped")
    private Boolean victimIsScoped;
    
    @JsonProperty("victim_is_moving")
    private Boolean victimIsMoving;
    
    @JsonProperty("victim_movement_speed")
    private Integer victimMovementSpeed;
    
    @JsonProperty("victim_has_primary")
    private Boolean victimHasPrimary;
    
    @JsonProperty("victim_has_secondary")
    private Boolean victimHasSecondary;
    
    @JsonProperty("victim_has_utility")
    private Boolean victimHasUtility;
    
    @JsonProperty("kill_advantage")
    private Double killAdvantage;
    
    @JsonProperty("distance_category")
    private String distanceCategory;
    
    @JsonProperty("is_eco_kill")
    private Boolean isEcoKill;
    
    @JsonProperty("is_trade_kill")
    private Boolean isTradeKill;
    
    @JsonProperty("is_clutch_situation")
    private Boolean isClutchSituation;
    
    // Constructor por defecto
    public KillContext() {}
    
    // Getters y Setters básicos (se pueden agregar más según necesidad)
    public Integer getKillTick() {
        return killTick;
    }
    
    public void setKillTick(Integer killTick) {
        this.killTick = killTick;
    }
    
    public String getAttackerName() {
        return attackerName;
    }
    
    public void setAttackerName(String attackerName) {
        this.attackerName = attackerName;
    }
    
    public String getVictimName() {
        return victimName;
    }
    
    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
    }
    
    public String getPlace() {
        return place;
    }
    
    public void setPlace(String place) {
        this.place = place;
    }
    
    public Boolean getHeadshot() {
        return headshot;
    }
    
    public void setHeadshot(Boolean headshot) {
        this.headshot = headshot;
    }
    
    public String getAttackerWeapon() {
        return attackerWeapon;
    }
    
    public void setAttackerWeapon(String attackerWeapon) {
        this.attackerWeapon = attackerWeapon;
    }
    
    public String getVictimWeapon() {
        return victimWeapon;
    }
    
    public void setVictimWeapon(String victimWeapon) {
        this.victimWeapon = victimWeapon;
    }
    
    public Double getAttackerX() {
        return attackerX;
    }
    
    public void setAttackerX(Double attackerX) {
        this.attackerX = attackerX;
    }
    
    public Double getAttackerY() {
        return attackerY;
    }
    
    public void setAttackerY(Double attackerY) {
        this.attackerY = attackerY;
    }
    
    public Double getAttackerZ() {
        return attackerZ;
    }
    
    public void setAttackerZ(Double attackerZ) {
        this.attackerZ = attackerZ;
    }
    
    public Double getAttackerHealth() {
        return attackerHealth;
    }
    
    public void setAttackerHealth(Double attackerHealth) {
        this.attackerHealth = attackerHealth;
    }
    
    public Integer getAttackerVelX() {
        return attackerVelX;
    }
    
    public void setAttackerVelX(Integer attackerVelX) {
        this.attackerVelX = attackerVelX;
    }
    
    public Integer getAttackerVelY() {
        return attackerVelY;
    }
    
    public void setAttackerVelY(Integer attackerVelY) {
        this.attackerVelY = attackerVelY;
    }
    
    public Double getVictimX() {
        return victimX;
    }
    
    public void setVictimX(Double victimX) {
        this.victimX = victimX;
    }
    
    public Double getVictimY() {
        return victimY;
    }
    
    public void setVictimY(Double victimY) {
        this.victimY = victimY;
    }
    
    public Double getVictimZ() {
        return victimZ;
    }
    
    public void setVictimZ(Double victimZ) {
        this.victimZ = victimZ;
    }
    
    public Double getVictimHealth() {
        return victimHealth;
    }
    
    public void setVictimHealth(Double victimHealth) {
        this.victimHealth = victimHealth;
    }
    
    public Double getTimeInRoundS() {
        return timeInRoundS;
    }
    
    public void setTimeInRoundS(Double timeInRoundS) {
        this.timeInRoundS = timeInRoundS;
    }
    
    public Double getDistanceXy() {
        return distanceXy;
    }
    
    public void setDistanceXy(Double distanceXy) {
        this.distanceXy = distanceXy;
    }
    
    public Double getApproachAlignDeg() {
        return approachAlignDeg;
    }
    
    public void setApproachAlignDeg(Double approachAlignDeg) {
        this.approachAlignDeg = approachAlignDeg;
    }
    
    public Boolean getFlashNear() {
        return flashNear;
    }
    
    public void setFlashNear(Boolean flashNear) {
        this.flashNear = flashNear;
    }
    
    public Boolean getSmokeNear() {
        return smokeNear;
    }
    
    public void setSmokeNear(Boolean smokeNear) {
        this.smokeNear = smokeNear;
    }
    
    public Boolean getMolotovNear() {
        return molotovNear;
    }
    
    public void setMolotovNear(Boolean molotovNear) {
        this.molotovNear = molotovNear;
    }
    
    public Boolean getHeNear() {
        return heNear;
    }
    
    public void setHeNear(Boolean heNear) {
        this.heNear = heNear;
    }
    
    public Double getAttackerImageX() {
        return attackerImageX;
    }
    
    public void setAttackerImageX(Double attackerImageX) {
        this.attackerImageX = attackerImageX;
    }
    
    public Double getAttackerImageY() {
        return attackerImageY;
    }
    
    public void setAttackerImageY(Double attackerImageY) {
        this.attackerImageY = attackerImageY;
    }
    
    public Double getVictimImageX() {
        return victimImageX;
    }
    
    public void setVictimImageX(Double victimImageX) {
        this.victimImageX = victimImageX;
    }
    
    public Double getVictimImageY() {
        return victimImageY;
    }
    
    public void setVictimImageY(Double victimImageY) {
        this.victimImageY = victimImageY;
    }
    
    public DebugInfo getDebugAttacker() {
        return debugAttacker;
    }
    
    public void setDebugAttacker(DebugInfo debugAttacker) {
        this.debugAttacker = debugAttacker;
    }
    
    public DebugInfo getDebugVictim() {
        return debugVictim;
    }
    
    public void setDebugVictim(DebugInfo debugVictim) {
        this.debugVictim = debugVictim;
    }
    
    public Boolean getHadSoundCue() {
        return hadSoundCue;
    }
    
    public void setHadSoundCue(Boolean hadSoundCue) {
        this.hadSoundCue = hadSoundCue;
    }
    
    public List<String> getSoundCueTypes() {
        return soundCueTypes;
    }
    
    public void setSoundCueTypes(List<String> soundCueTypes) {
        this.soundCueTypes = soundCueTypes;
    }
    
    public Integer getSoundCueCount() {
        return soundCueCount;
    }
    
    public void setSoundCueCount(Integer soundCueCount) {
        this.soundCueCount = soundCueCount;
    }
    
    public Integer getLastSoundTick() {
        return lastSoundTick;
    }
    
    public void setLastSoundTick(Integer lastSoundTick) {
        this.lastSoundTick = lastSoundTick;
    }
    
    public Double getTimeSinceLastSound() {
        return timeSinceLastSound;
    }
    
    public void setTimeSinceLastSound(Double timeSinceLastSound) {
        this.timeSinceLastSound = timeSinceLastSound;
    }
    
    public Boolean getAttackerVisible() {
        return attackerVisible;
    }
    
    public void setAttackerVisible(Boolean attackerVisible) {
        this.attackerVisible = attackerVisible;
    }
    
    public Double getAttackerDistanceWhenHeard() {
        return attackerDistanceWhenHeard;
    }
    
    public void setAttackerDistanceWhenHeard(Double attackerDistanceWhenHeard) {
        this.attackerDistanceWhenHeard = attackerDistanceWhenHeard;
    }
    
    public Boolean getVictimWasAware() {
        return victimWasAware;
    }
    
    public void setVictimWasAware(Boolean victimWasAware) {
        this.victimWasAware = victimWasAware;
    }
    
    public Boolean getVictimWasWatching() {
        return victimWasWatching;
    }
    
    public void setVictimWasWatching(Boolean victimWasWatching) {
        this.victimWasWatching = victimWasWatching;
    }
    
    public Boolean getVictimWasBackstabbed() {
        return victimWasBackstabbed;
    }
    
    public void setVictimWasBackstabbed(Boolean victimWasBackstabbed) {
        this.victimWasBackstabbed = victimWasBackstabbed;
    }
    
    public Double getTimeSinceLastSight() {
        return timeSinceLastSight;
    }
    
    public void setTimeSinceLastSight(Double timeSinceLastSight) {
        this.timeSinceLastSight = timeSinceLastSight;
    }
    
    public Double getAwarenessConfidence() {
        return awarenessConfidence;
    }
    
    public void setAwarenessConfidence(Double awarenessConfidence) {
        this.awarenessConfidence = awarenessConfidence;
    }
    
    public Double getAngleToAttacker() {
        return angleToAttacker;
    }
    
    public void setAngleToAttacker(Double angleToAttacker) {
        this.angleToAttacker = angleToAttacker;
    }
    
    public Integer getVictimViewAngle() {
        return victimViewAngle;
    }
    
    public void setVictimViewAngle(Integer victimViewAngle) {
        this.victimViewAngle = victimViewAngle;
    }
    
    public Double getAngleDifference() {
        return angleDifference;
    }
    
    public void setAngleDifference(Double angleDifference) {
        this.angleDifference = angleDifference;
    }
    
    public Integer getAwarenessDetectedAtTick() {
        return awarenessDetectedAtTick;
    }
    
    public void setAwarenessDetectedAtTick(Integer awarenessDetectedAtTick) {
        this.awarenessDetectedAtTick = awarenessDetectedAtTick;
    }
    
    public Integer getRoundNumber() {
        return roundNumber;
    }
    
    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }
    
    public String getRoundPhase() {
        return roundPhase;
    }
    
    public void setRoundPhase(String roundPhase) {
        this.roundPhase = roundPhase;
    }
    
    public Boolean getBombPlanted() {
        return bombPlanted;
    }
    
    public void setBombPlanted(Boolean bombPlanted) {
        this.bombPlanted = bombPlanted;
    }
    
    public Double getTimeSinceBombPlant() {
        return timeSinceBombPlant;
    }
    
    public void setTimeSinceBombPlant(Double timeSinceBombPlant) {
        this.timeSinceBombPlant = timeSinceBombPlant;
    }
    
    public Double getTimeUntilBombExplode() {
        return timeUntilBombExplode;
    }
    
    public void setTimeUntilBombExplode(Double timeUntilBombExplode) {
        this.timeUntilBombExplode = timeUntilBombExplode;
    }
    
    public Integer getPlayersAliveT() {
        return playersAliveT;
    }
    
    public void setPlayersAliveT(Integer playersAliveT) {
        this.playersAliveT = playersAliveT;
    }
    
    public Integer getPlayersAliveCt() {
        return playersAliveCt;
    }
    
    public void setPlayersAliveCt(Integer playersAliveCt) {
        this.playersAliveCt = playersAliveCt;
    }
    
    public Double getRoundWinProbability() {
        return roundWinProbability;
    }
    
    public void setRoundWinProbability(Double roundWinProbability) {
        this.roundWinProbability = roundWinProbability;
    }
    
    public Integer getMatchScoreT() {
        return matchScoreT;
    }
    
    public void setMatchScoreT(Integer matchScoreT) {
        this.matchScoreT = matchScoreT;
    }
    
    public Integer getMatchScoreCt() {
        return matchScoreCt;
    }
    
    public void setMatchScoreCt(Integer matchScoreCt) {
        this.matchScoreCt = matchScoreCt;
    }
    
    public String getTeam1Name() {
        return team1Name;
    }
    
    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }
    
    public String getTeam2Name() {
        return team2Name;
    }
    
    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    public Boolean getFlashActive() {
        return flashActive;
    }
    
    public void setFlashActive(Boolean flashActive) {
        this.flashActive = flashActive;
    }
    
    public Boolean getSmokeActive() {
        return smokeActive;
    }
    
    public void setSmokeActive(Boolean smokeActive) {
        this.smokeActive = smokeActive;
    }
    
    public Boolean getMolotovActive() {
        return molotovActive;
    }
    
    public void setMolotovActive(Boolean molotovActive) {
        this.molotovActive = molotovActive;
    }
    
    public Boolean getHeActive() {
        return heActive;
    }
    
    public void setHeActive(Boolean heActive) {
        this.heActive = heActive;
    }
    
    public Integer getUtilityCount() {
        return utilityCount;
    }
    
    public void setUtilityCount(Integer utilityCount) {
        this.utilityCount = utilityCount;
    }
    
    public Double getClosestUtilityDistance() {
        return closestUtilityDistance;
    }
    
    public void setClosestUtilityDistance(Double closestUtilityDistance) {
        this.closestUtilityDistance = closestUtilityDistance;
    }
    
    public String getUtilityThrower() {
        return utilityThrower;
    }
    
    public void setUtilityThrower(String utilityThrower) {
        this.utilityThrower = utilityThrower;
    }
    
    public Double getTimeSinceUtility() {
        return timeSinceUtility;
    }
    
    public void setTimeSinceUtility(Double timeSinceUtility) {
        this.timeSinceUtility = timeSinceUtility;
    }
    
    public Boolean getUtilityAffectingKill() {
        return utilityAffectingKill;
    }
    
    public void setUtilityAffectingKill(Boolean utilityAffectingKill) {
        this.utilityAffectingKill = utilityAffectingKill;
    }
    
    public Boolean getAttackerIsAlive() {
        return attackerIsAlive;
    }
    
    public void setAttackerIsAlive(Boolean attackerIsAlive) {
        this.attackerIsAlive = attackerIsAlive;
    }
    
    public Boolean getAttackerIsDucking() {
        return attackerIsDucking;
    }
    
    public void setAttackerIsDucking(Boolean attackerIsDucking) {
        this.attackerIsDucking = attackerIsDucking;
    }
    
    public Boolean getAttackerIsScoped() {
        return attackerIsScoped;
    }
    
    public void setAttackerIsScoped(Boolean attackerIsScoped) {
        this.attackerIsScoped = attackerIsScoped;
    }
    
    public Boolean getAttackerIsMoving() {
        return attackerIsMoving;
    }
    
    public void setAttackerIsMoving(Boolean attackerIsMoving) {
        this.attackerIsMoving = attackerIsMoving;
    }
    
    public Integer getAttackerMovementSpeed() {
        return attackerMovementSpeed;
    }
    
    public void setAttackerMovementSpeed(Integer attackerMovementSpeed) {
        this.attackerMovementSpeed = attackerMovementSpeed;
    }
    
    public Boolean getAttackerHasPrimary() {
        return attackerHasPrimary;
    }
    
    public void setAttackerHasPrimary(Boolean attackerHasPrimary) {
        this.attackerHasPrimary = attackerHasPrimary;
    }
    
    public Boolean getAttackerHasSecondary() {
        return attackerHasSecondary;
    }
    
    public void setAttackerHasSecondary(Boolean attackerHasSecondary) {
        this.attackerHasSecondary = attackerHasSecondary;
    }
    
    public Boolean getAttackerHasUtility() {
        return attackerHasUtility;
    }
    
    public void setAttackerHasUtility(Boolean attackerHasUtility) {
        this.attackerHasUtility = attackerHasUtility;
    }
    
    public Boolean getVictimIsAlive() {
        return victimIsAlive;
    }
    
    public void setVictimIsAlive(Boolean victimIsAlive) {
        this.victimIsAlive = victimIsAlive;
    }
    
    public Boolean getVictimIsDucking() {
        return victimIsDucking;
    }
    
    public void setVictimIsDucking(Boolean victimIsDucking) {
        this.victimIsDucking = victimIsDucking;
    }
    
    public Boolean getVictimIsScoped() {
        return victimIsScoped;
    }
    
    public void setVictimIsScoped(Boolean victimIsScoped) {
        this.victimIsScoped = victimIsScoped;
    }
    
    public Boolean getVictimIsMoving() {
        return victimIsMoving;
    }
    
    public void setVictimIsMoving(Boolean victimIsMoving) {
        this.victimIsMoving = victimIsMoving;
    }
    
    public Integer getVictimMovementSpeed() {
        return victimMovementSpeed;
    }
    
    public void setVictimMovementSpeed(Integer victimMovementSpeed) {
        this.victimMovementSpeed = victimMovementSpeed;
    }
    
    public Boolean getVictimHasPrimary() {
        return victimHasPrimary;
    }
    
    public void setVictimHasPrimary(Boolean victimHasPrimary) {
        this.victimHasPrimary = victimHasPrimary;
    }
    
    public Boolean getVictimHasSecondary() {
        return victimHasSecondary;
    }
    
    public void setVictimHasSecondary(Boolean victimHasSecondary) {
        this.victimHasSecondary = victimHasSecondary;
    }
    
    public Boolean getVictimHasUtility() {
        return victimHasUtility;
    }
    
    public void setVictimHasUtility(Boolean victimHasUtility) {
        this.victimHasUtility = victimHasUtility;
    }
    
    public Double getKillAdvantage() {
        return killAdvantage;
    }
    
    public void setKillAdvantage(Double killAdvantage) {
        this.killAdvantage = killAdvantage;
    }
    
    public String getDistanceCategory() {
        return distanceCategory;
    }
    
    public void setDistanceCategory(String distanceCategory) {
        this.distanceCategory = distanceCategory;
    }
    
    public Boolean getIsEcoKill() {
        return isEcoKill;
    }
    
    public void setIsEcoKill(Boolean isEcoKill) {
        this.isEcoKill = isEcoKill;
    }
    
    public Boolean getIsTradeKill() {
        return isTradeKill;
    }
    
    public void setIsTradeKill(Boolean isTradeKill) {
        this.isTradeKill = isTradeKill;
    }
    
    public Boolean getIsClutchSituation() {
        return isClutchSituation;
    }
    
    public void setIsClutchSituation(Boolean isClutchSituation) {
        this.isClutchSituation = isClutchSituation;
    }
    
    @Override
    public String toString() {
        return "KillContext{" +
                "killTick=" + killTick +
                ", attackerName='" + attackerName + '\'' +
                ", victimName='" + victimName + '\'' +
                ", side='" + side + '\'' +
                ", place='" + place + '\'' +
                ", headshot=" + headshot +
                ", attackerWeapon='" + attackerWeapon + '\'' +
                ", victimWeapon='" + victimWeapon + '\'' +
                ", attackerX=" + attackerX +
                ", attackerY=" + attackerY +
                ", attackerZ=" + attackerZ +
                ", attackerHealth=" + attackerHealth +
                ", attackerVelX=" + attackerVelX +
                ", attackerVelY=" + attackerVelY +
                ", victimX=" + victimX +
                ", victimY=" + victimY +
                ", victimZ=" + victimZ +
                ", victimHealth=" + victimHealth +
                ", timeInRoundS=" + timeInRoundS +
                ", distanceXy=" + distanceXy +
                ", approachAlignDeg=" + approachAlignDeg +
                ", flashNear=" + flashNear +
                ", smokeNear=" + smokeNear +
                ", molotovNear=" + molotovNear +
                ", heNear=" + heNear +
                ", attackerImageX=" + attackerImageX +
                ", attackerImageY=" + attackerImageY +
                ", victimImageX=" + victimImageX +
                ", victimImageY=" + victimImageY +
                ", debugAttacker=" + debugAttacker +
                ", debugVictim=" + debugVictim +
                ", hadSoundCue=" + hadSoundCue +
                ", soundCueTypes=" + soundCueTypes +
                ", soundCueCount=" + soundCueCount +
                ", lastSoundTick=" + lastSoundTick +
                ", timeSinceLastSound=" + timeSinceLastSound +
                ", attackerVisible=" + attackerVisible +
                ", attackerDistanceWhenHeard=" + attackerDistanceWhenHeard +
                ", victimWasAware=" + victimWasAware +
                ", victimWasWatching=" + victimWasWatching +
                ", victimWasBackstabbed=" + victimWasBackstabbed +
                ", timeSinceLastSight=" + timeSinceLastSight +
                ", awarenessConfidence=" + awarenessConfidence +
                ", angleToAttacker=" + angleToAttacker +
                ", victimViewAngle=" + victimViewAngle +
                ", angleDifference=" + angleDifference +
                ", awarenessDetectedAtTick=" + awarenessDetectedAtTick +
                ", roundNumber=" + roundNumber +
                ", roundPhase='" + roundPhase + '\'' +
                ", bombPlanted=" + bombPlanted +
                ", timeSinceBombPlant=" + timeSinceBombPlant +
                ", timeUntilBombExplode=" + timeUntilBombExplode +
                ", playersAliveT=" + playersAliveT +
                ", playersAliveCt=" + playersAliveCt +
                ", roundWinProbability=" + roundWinProbability +
                ", matchScoreT=" + matchScoreT +
                ", matchScoreCt=" + matchScoreCt +
                ", team1Name='" + team1Name + '\'' +
                ", team2Name='" + team2Name + '\'' +
                ", gameId='" + gameId + '\'' +
                ", flashActive=" + flashActive +
                ", smokeActive=" + smokeActive +
                ", molotovActive=" + molotovActive +
                ", heActive=" + heActive +
                ", utilityCount=" + utilityCount +
                ", closestUtilityDistance=" + closestUtilityDistance +
                ", utilityThrower='" + utilityThrower + '\'' +
                ", timeSinceUtility=" + timeSinceUtility +
                ", utilityAffectingKill=" + utilityAffectingKill +
                ", attackerIsAlive=" + attackerIsAlive +
                ", attackerIsDucking=" + attackerIsDucking +
                ", attackerIsScoped=" + attackerIsScoped +
                ", attackerIsMoving=" + attackerIsMoving +
                ", attackerMovementSpeed=" + attackerMovementSpeed +
                ", attackerHasPrimary=" + attackerHasPrimary +
                ", attackerHasSecondary=" + attackerHasSecondary +
                ", attackerHasUtility=" + attackerHasUtility +
                ", victimIsAlive=" + victimIsAlive +
                ", victimIsDucking=" + victimIsDucking +
                ", victimIsScoped=" + victimIsScoped +
                ", victimIsMoving=" + victimIsMoving +
                ", victimMovementSpeed=" + victimMovementSpeed +
                ", victimHasPrimary=" + victimHasPrimary +
                ", victimHasSecondary=" + victimHasSecondary +
                ", victimHasUtility=" + victimHasUtility +
                ", killAdvantage=" + killAdvantage +
                ", distanceCategory='" + distanceCategory + '\'' +
                ", isEcoKill=" + isEcoKill +
                ", isTradeKill=" + isTradeKill +
                ", isClutchSituation=" + isClutchSituation +
                '}';
    }
}
