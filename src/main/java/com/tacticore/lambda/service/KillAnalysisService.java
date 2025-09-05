package com.tacticore.lambda.service;

import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.KillPredictionEntity;
import com.tacticore.lambda.model.dto.KillAnalysisDto;
import com.tacticore.lambda.model.dto.PlayerStatsDto;
import com.tacticore.lambda.model.dto.RoundAnalysisDto;
import com.tacticore.lambda.repository.KillPredictionRepository;
import com.tacticore.lambda.repository.KillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KillAnalysisService {
    
    @Autowired
    private KillRepository killRepository;
    
    @Autowired
    private KillPredictionRepository killPredictionRepository;
    
    public KillAnalysisDto getOverallAnalysis() {
        Long totalKills = killRepository.getTotalKills();
        Long totalHeadshots = killRepository.getTotalHeadshots();
        Double headshotRate = totalKills > 0 ? (double) totalHeadshots / totalKills * 100 : 0.0;
        Double averageDistance = killRepository.getAverageDistance();
        Double averageTimeInRound = killRepository.getAverageTimeInRound();
        
        List<Object[]> weaponStatsRaw = killRepository.getWeaponUsageStats();
        List<Map<String, Object>> weaponStats = weaponStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("weapon", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        
        List<Object[]> locationStatsRaw = killRepository.getLocationStats();
        List<Map<String, Object>> locationStats = locationStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("location", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        
        List<Object[]> roundStatsRaw = killRepository.getKillsPerRound();
        List<Map<String, Object>> roundStats = roundStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("round", row[0]);
                map.put("kills", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        
        List<Object[]> sideStatsRaw = killRepository.getKillsBySide();
        List<Map<String, Object>> sideStats = sideStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("side", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        
        List<Map<String, Object>> topPlayers = getTopPlayers();
        List<Map<String, Object>> predictionStats = getPredictionStats();
        
        return new KillAnalysisDto(
            totalKills, totalHeadshots, headshotRate, averageDistance, averageTimeInRound,
            weaponStats, locationStats, roundStats, sideStats, topPlayers, predictionStats
        );
    }
    
    public PlayerStatsDto getPlayerStats(String playerName) {
        Long kills = killRepository.countKillsByAttacker(playerName);
        Long deaths = killRepository.countDeathsByVictim(playerName);
        Double kdRatio = deaths > 0 ? (double) kills / deaths : kills > 0 ? (double) kills : 0.0;
        
        Long headshots = killRepository.countHeadshotsByAttacker(playerName);
        Double headshotRate = kills > 0 ? (double) headshots / kills * 100 : 0.0;
        
        // Calcular distancia promedio
        List<KillEntity> playerKills = killRepository.findByAttacker(playerName);
        Double averageDistance = playerKills.stream()
            .filter(k -> k.getDistance() != null && k.getDistance() > 0)
            .mapToDouble(KillEntity::getDistance)
            .average()
            .orElse(0.0);
        
        // Arma favorita
        String favoriteWeapon = playerKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getWeapon, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        
        // Calcular score de rendimiento
        Double performanceScore = calculatePerformanceScore(kills, deaths, headshotRate, averageDistance);
        
        return new PlayerStatsDto(
            playerName, kills, deaths, kdRatio, headshots, headshotRate,
            averageDistance, favoriteWeapon, performanceScore
        );
    }
    
    public RoundAnalysisDto getRoundAnalysis(Integer roundNumber) {
        List<KillEntity> roundKills = killRepository.findByRound(roundNumber);
        Long totalKills = (long) roundKills.size();
        
        // Duración de la ronda (tiempo máximo - tiempo mínimo)
        Double duration = roundKills.stream()
            .mapToDouble(KillEntity::getTimeInRound)
            .max()
            .orElse(0.0);
        
        // Jugador más activo
        String mostActivePlayer = roundKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getAttacker, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        
        // Hot spots
        List<Map<String, Object>> hotSpots = roundKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getPlace, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("location", entry.getKey());
                map.put("kills", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());
        
        // Distribución de armas
        Map<String, Long> weaponDistribution = roundKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getWeapon, Collectors.counting()));
        
        // Balance CT/T
        Map<String, Long> ctTBalance = roundKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getSide, Collectors.counting()));
        
        // Tasa de headshots
        Long headshots = roundKills.stream()
            .mapToLong(k -> k.getHeadshot() ? 1 : 0)
            .sum();
        Double headshotRate = totalKills > 0 ? (double) headshots / totalKills * 100 : 0.0;
        
        // Distancia promedio
        Double averageDistance = roundKills.stream()
            .filter(k -> k.getDistance() != null && k.getDistance() > 0)
            .mapToDouble(KillEntity::getDistance)
            .average()
            .orElse(0.0);
        
        return new RoundAnalysisDto(
            roundNumber, totalKills, duration, mostActivePlayer, hotSpots,
            weaponDistribution, ctTBalance, headshotRate, averageDistance
        );
    }
    
    public List<Map<String, Object>> getTopPlayers() {
        List<KillEntity> allKills = killRepository.findAll();
        
        Map<String, Long> killsByPlayer = allKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getAttacker, Collectors.counting()));
        
        Map<String, Long> deathsByPlayer = allKills.stream()
            .collect(Collectors.groupingBy(KillEntity::getVictim, Collectors.counting()));
        
        return killsByPlayer.entrySet().stream()
            .map(entry -> {
                String player = entry.getKey();
                Long kills = entry.getValue();
                Long deaths = deathsByPlayer.getOrDefault(player, 0L);
                Double kdRatio = deaths > 0 ? (double) kills / deaths : kills > 0 ? (double) kills : 0.0;
                
                Map<String, Object> map = new HashMap<>();
                map.put("player", player);
                map.put("kills", kills);
                map.put("deaths", deaths);
                map.put("kd_ratio", kdRatio);
                return map;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("kd_ratio"), (Double) a.get("kd_ratio")))
            .limit(10)
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getPredictionStats() {
        List<Object[]> predictionStatsRaw = killPredictionRepository.getTopPredictionStats();
        List<Object[]> confidenceStatsRaw = killPredictionRepository.getAverageConfidenceByLabel();
        
        Map<String, Double> confidenceMap = confidenceStatsRaw.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Double) row[1]
            ));
        
        return predictionStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("label", row[0]);
                map.put("count", row[1]);
                map.put("average_confidence", confidenceMap.getOrDefault(row[0], 0.0));
                return map;
            })
            .collect(Collectors.toList());
    }
    
    private Double calculatePerformanceScore(Long kills, Long deaths, Double headshotRate, Double averageDistance) {
        double baseScore = 100.0;
        double kdBonus = kills > 0 && deaths > 0 ? Math.min((double) kills / deaths * 10, 50) : kills * 5;
        double headshotBonus = headshotRate * 0.5;
        double distanceBonus = Math.min(averageDistance / 100, 20);
        
        return baseScore + kdBonus + headshotBonus + distanceBonus;
    }
}
