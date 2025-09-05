package com.tacticore.lambda.service;

import com.tacticore.lambda.model.KillEntity;
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
    
    public KillAnalysisDto getAnalysisByUser(String user) {
        KillAnalysisDto analysis = new KillAnalysisDto();
        
        // Estadísticas básicas por usuario
        Long totalKillsLong = killRepository.countKillsByUser(user);
        Long totalHeadshotsLong = killRepository.countHeadshotsByUser(user);
        Double averageDistance = killRepository.getAverageDistanceByUser(user);
        Double averageTimeInRound = killRepository.getAverageTimeInRoundByUser(user);
        
        Long totalKills = totalKillsLong != null ? totalKillsLong : 0L;
        Long totalHeadshots = totalHeadshotsLong != null ? totalHeadshotsLong : 0L;
        
        analysis.setTotalKills(totalKills);
        analysis.setTotalHeadshots(totalHeadshots);
        analysis.setHeadshotRate(totalKills > 0 ? (double) totalHeadshots / totalKills * 100 : 0);
        analysis.setAverageDistance(averageDistance != null ? averageDistance : 0.0);
        analysis.setAverageTimeInRound(averageTimeInRound != null ? averageTimeInRound : 0.0);
        
        // Estadísticas de armas por usuario
        List<Object[]> weaponStatsRaw = killRepository.getWeaponUsageStatsByUser(user);
        List<Map<String, Object>> weaponStats = weaponStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("weapon", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        analysis.setWeaponStats(weaponStats);
        
        // Estadísticas de ubicaciones por usuario
        List<Object[]> locationStatsRaw = killRepository.getLocationStatsByUser(user);
        List<Map<String, Object>> locationStats = locationStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("location", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        analysis.setLocationStats(locationStats);
        
        // Estadísticas por ronda por usuario
        List<Object[]> roundStatsRaw = killRepository.getKillsPerRoundByUser(user);
        List<Map<String, Object>> roundStats = roundStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("round", row[0]);
                map.put("kills", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        analysis.setRoundStats(roundStats);
        
        // Estadísticas por lado por usuario
        List<Object[]> sideStatsRaw = killRepository.getKillsBySideByUser(user);
        List<Map<String, Object>> sideStats = sideStatsRaw.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("side", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
        analysis.setSideStats(sideStats);
        
        // Solo este usuario en top players
        PlayerStatsDto userStats = getPlayerStats(user);
        List<Map<String, Object>> topPlayers = new ArrayList<>();
        if (userStats != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("player", userStats.getPlayerName());
            userMap.put("kills", userStats.getKills());
            userMap.put("deaths", userStats.getDeaths());
            userMap.put("headshots", userStats.getHeadshots());
            userMap.put("headshot_rate", userStats.getHeadshotRate());
            userMap.put("average_distance", userStats.getAverageDistance());
            userMap.put("performance_score", userStats.getPerformanceScore());
            topPlayers.add(userMap);
        }
        analysis.setTopPlayers(topPlayers);
        
        // Estadísticas de predicciones (solo para este usuario)
        analysis.setPredictionStats(getPredictionStatsByUser(user));
        
        return analysis;
    }
    
    public List<KillEntity> getKillsByUser(String user) {
        return killRepository.findByUser(user);
    }
    
    public List<KillEntity> getKillsByUserAndRound(String user, Integer round) {
        return killRepository.findByUserAndRound(user, round);
    }
    
    public List<KillEntity> getAllKills() {
        return killRepository.findAll();
    }
    
    public List<String> getAllUsers() {
        List<String> attackers = killRepository.findAllAttackers();
        List<String> victims = killRepository.findAllVictims();
        
        Set<String> allUsers = new HashSet<>();
        allUsers.addAll(attackers);
        allUsers.addAll(victims);
        
        return allUsers.stream().sorted().collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> getPredictionStatsByUser(String user) {
        // Implementar estadísticas de predicciones por usuario
        return new ArrayList<>();
    }
    
    private Double calculatePerformanceScore(Long kills, Long deaths, Double headshotRate, Double averageDistance) {
        // Normalized performance score: scale 1-10
        // Base score starts at 1.0
        double baseScore = 1.0;
        
        // K/D ratio bonus (max 4 points)
        double kdBonus = 0.0;
        if (kills > 0 && deaths > 0) {
            double kdRatio = (double) kills / deaths;
            kdBonus = Math.min(kdRatio * 0.8, 4.0); // Max 4 points from K/D
        } else if (kills > 0) {
            kdBonus = Math.min(kills * 0.1, 4.0); // Bonus for kills without deaths
        }
        
        // Headshot rate bonus (max 2 points)
        double headshotBonus = Math.min(headshotRate * 0.02, 2.0);
        
        // Distance bonus (max 2 points)
        double distanceBonus = Math.min(averageDistance / 500, 2.0);
        
        // Random variance (max 1 point)
        double randomVariance = Math.random() * 1.0;
        
        double rawScore = baseScore + kdBonus + headshotBonus + distanceBonus + randomVariance;
        return Math.min(Math.max(rawScore, 1.0), 10.0); // Ensure range 1.0-10.0
    }
}
