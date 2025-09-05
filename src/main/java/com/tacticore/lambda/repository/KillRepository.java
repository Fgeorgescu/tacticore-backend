package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.KillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KillRepository extends JpaRepository<KillEntity, Long> {
    
    Optional<KillEntity> findByKillId(String killId);
    
    List<KillEntity> findByAttacker(String attacker);
    
    List<KillEntity> findByVictim(String victim);
    
    List<KillEntity> findByRound(Integer round);
    
    List<KillEntity> findByWeapon(String weapon);
    
    List<KillEntity> findByPlace(String place);
    
    List<KillEntity> findBySide(String side);
    
    List<KillEntity> findByHeadshot(Boolean headshot);
    
    @Query("SELECT k FROM KillEntity k WHERE k.round BETWEEN :startRound AND :endRound")
    List<KillEntity> findByRoundRange(@Param("startRound") Integer startRound, @Param("endRound") Integer endRound);
    
    @Query("SELECT k FROM KillEntity k WHERE k.distance BETWEEN :minDistance AND :maxDistance")
    List<KillEntity> findByDistanceRange(@Param("minDistance") Double minDistance, @Param("maxDistance") Double maxDistance);
    
    @Query("SELECT k FROM KillEntity k WHERE k.timeInRound BETWEEN :minTime AND :maxTime")
    List<KillEntity> findByTimeInRoundRange(@Param("minTime") Double minTime, @Param("maxTime") Double maxTime);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.attacker = :player")
    Long countKillsByAttacker(@Param("player") String player);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.victim = :player")
    Long countDeathsByVictim(@Param("player") String player);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.attacker = :player AND k.headshot = true")
    Long countHeadshotsByAttacker(@Param("player") String player);
    
    @Query("SELECT k.weapon, COUNT(k) FROM KillEntity k GROUP BY k.weapon ORDER BY COUNT(k) DESC")
    List<Object[]> getWeaponUsageStats();
    
    @Query("SELECT k.place, COUNT(k) FROM KillEntity k GROUP BY k.place ORDER BY COUNT(k) DESC")
    List<Object[]> getLocationStats();
    
    @Query("SELECT k.round, COUNT(k) FROM KillEntity k GROUP BY k.round ORDER BY k.round")
    List<Object[]> getKillsPerRound();
    
    @Query("SELECT k.side, COUNT(k) FROM KillEntity k GROUP BY k.side")
    List<Object[]> getKillsBySide();
    
    @Query("SELECT AVG(k.distance) FROM KillEntity k WHERE k.distance > 0")
    Double getAverageDistance();
    
    @Query("SELECT AVG(k.timeInRound) FROM KillEntity k")
    Double getAverageTimeInRound();
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.headshot = true")
    Long getTotalHeadshots();
    
    @Query("SELECT COUNT(k) FROM KillEntity k")
    Long getTotalKills();
    
    // Consultas por usuario (attacker o victim)
    @Query("SELECT k FROM KillEntity k WHERE k.attacker = :user OR k.victim = :user")
    List<KillEntity> findByUser(@Param("user") String user);
    
    @Query("SELECT k FROM KillEntity k WHERE (k.attacker = :user OR k.victim = :user) AND k.round = :round")
    List<KillEntity> findByUserAndRound(@Param("user") String user, @Param("round") Integer round);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.attacker = :user")
    Long countKillsByUser(@Param("user") String user);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.victim = :user")
    Long countDeathsByUser(@Param("user") String user);
    
    @Query("SELECT COUNT(k) FROM KillEntity k WHERE k.headshot = true AND k.attacker = :user")
    Long countHeadshotsByUser(@Param("user") String user);
    
    @Query("SELECT AVG(k.distance) FROM KillEntity k WHERE k.distance > 0 AND k.attacker = :user")
    Double getAverageDistanceByUser(@Param("user") String user);
    
    @Query("SELECT AVG(k.timeInRound) FROM KillEntity k WHERE k.attacker = :user")
    Double getAverageTimeInRoundByUser(@Param("user") String user);
    
    @Query("SELECT k.weapon, COUNT(k) FROM KillEntity k WHERE k.attacker = :user GROUP BY k.weapon ORDER BY COUNT(k) DESC")
    List<Object[]> getWeaponUsageStatsByUser(@Param("user") String user);
    
    @Query("SELECT k.place, COUNT(k) FROM KillEntity k WHERE k.attacker = :user GROUP BY k.place ORDER BY COUNT(k) DESC")
    List<Object[]> getLocationStatsByUser(@Param("user") String user);
    
    @Query("SELECT k.round, COUNT(k) FROM KillEntity k WHERE k.attacker = :user GROUP BY k.round ORDER BY k.round")
    List<Object[]> getKillsPerRoundByUser(@Param("user") String user);
    
    @Query("SELECT k.side, COUNT(k) FROM KillEntity k WHERE k.attacker = :user GROUP BY k.side")
    List<Object[]> getKillsBySideByUser(@Param("user") String user);
    
    // Consultas para obtener usuarios únicos
    @Query("SELECT DISTINCT k.attacker FROM KillEntity k ORDER BY k.attacker")
    List<String> findAllAttackers();
    
    @Query("SELECT DISTINCT k.victim FROM KillEntity k ORDER BY k.victim")
    List<String> findAllVictims();
}
