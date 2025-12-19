package com.tacticore.lambda.service;

import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatabaseMatchService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private KillRepository killRepository;
    
    @Autowired
    private UserService userService;
    
    public List<MatchDto> getAllMatches() {
        List<MatchEntity> entities = matchRepository.findAll();
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<MatchDto> getMatchById(String matchId) {
        return matchRepository.findByMatchId(matchId)
                .map(this::convertToDto);
    }
    
    public boolean existsMatch(String matchId) {
        return matchRepository.existsByMatchId(matchId);
    }
    
    public void deleteMatch(String matchId) {
        matchRepository.findByMatchId(matchId)
                .ifPresent(matchRepository::delete);
    }
    
    public MatchEntity saveMatch(MatchEntity matchEntity) {
        return matchRepository.save(matchEntity);
    }
    
    /**
     * Obtiene el JSON de la respuesta del ML service para un match
     */
    public String getMlResponseJson(String matchId) {
        return matchRepository.findByMatchId(matchId)
                .map(MatchEntity::getMlResponseJson)
                .orElse(null);
    }
    
    /**
     * Actualiza un match existente con resultados y guarda todos sus kills
     */
    @Transactional
    public void updateMatchWithKills(String matchId, Integer totalKills, Integer tickrate, String mapName, List<KillEntity> killEntities) {
        updateMatchWithKills(matchId, totalKills, tickrate, mapName, killEntities, null);
    }
    
    /**
     * Actualiza un match existente con resultados y guarda todos sus kills
     * Calcula goodPlays y badPlays desde las predicciones del ML
     */
    @Transactional
    public void updateMatchWithKills(String matchId, Integer totalKills, Integer tickrate, String mapName, List<KillEntity> killEntities, Map<String, Object> mlResponse) {
        // Buscar el match existente
        Optional<MatchEntity> matchOpt = matchRepository.findByMatchId(matchId);
        if (matchOpt.isPresent()) {
            MatchEntity match = matchOpt.get();
            
            match.setTotalKills(totalKills);
            match.setTickrate(tickrate);
            match.setMapName(mapName);
            match.setStatus("completed");
            
            if (mlResponse != null) {
                int[] playCounts = calculatePlaysFromPredictions(mlResponse);
                match.setGoodPlays(playCounts[0]);
                match.setBadPlays(playCounts[1]);
                
                try {
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    String mlResponseJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mlResponse);
                    match.setMlResponseJson(mlResponseJson);
                } catch (Exception e) {
                    System.err.println("Error serializing ML response to JSON: " + e.getMessage());
                }
            } else {
                match.setGoodPlays(calculateGoodPlays(totalKills));
                match.setBadPlays(calculateBadPlays(totalKills));
            }
            
            matchRepository.save(match);
            
            if (killEntities != null && !killEntities.isEmpty()) {
                killRepository.saveAll(killEntities);
                updateUserStatsFromKills(killEntities);
            }
        } else {
            throw new RuntimeException("Match no encontrado: " + matchId);
        }
    }
    
    public void updateMatchWithResults(String matchId, int totalKills, int tickrate, String mapName) {
        matchRepository.findByMatchId(matchId).ifPresent(match -> {
            match.setTotalKills(totalKills);
            match.setTickrate(tickrate);
            match.setMapName(mapName);
            match.setStatus("completed");
            matchRepository.save(match);
        });
    }
    
    public void updateMatchWithError(String matchId, String errorMessage) {
        matchRepository.findByMatchId(matchId).ifPresent(match -> {
            match.setStatus("failed");
            matchRepository.save(match);
        });
    }
    
    public String getMatchStatus(String matchId) {
        return matchRepository.findByMatchId(matchId)
                .map(MatchEntity::getStatus)
                .orElse("not_found");
    }
    
    public List<MatchDto> getMatchesByUser(String user) {
        List<MatchEntity> entities = matchRepository.findAll();
        return entities.stream()
                .filter(match -> killRepository.existsByMatchIdAndAttackerName(match.getMatchId(), user))
                .map(entity -> convertToDtoForUser(entity, user))
                .collect(Collectors.toList());
    }
    
    private MatchDto convertToDto(MatchEntity entity) {
        MatchDto dto = new MatchDto();
        dto.setId(entity.getMatchId());
        dto.setFileName(entity.getFileName());
        dto.setHasVideo(entity.getHasVideo());
        dto.setMap(entity.getMapName());
        dto.setGameType("Ranked");
        
        Integer totalKills = entity.getTotalKills();
        if (totalKills != null) {
            dto.setKills(totalKills);
            dto.setDeaths(calculateDeaths(totalKills));
            
            if (entity.getGoodPlays() != null && entity.getBadPlays() != null && 
                (entity.getGoodPlays() > 0 || entity.getBadPlays() > 0)) {
                dto.setGoodPlays(entity.getGoodPlays());
                dto.setBadPlays(entity.getBadPlays());
            } else {
                String mlResponseJson = entity.getMlResponseJson();
                if (mlResponseJson != null && !mlResponseJson.isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> mlResponse = objectMapper.readValue(mlResponseJson, Map.class);
                        int[] playCounts = calculatePlaysFromPredictions(mlResponse);
                        dto.setGoodPlays(playCounts[0]);
                        dto.setBadPlays(playCounts[1]);
                        
                        entity.setGoodPlays(playCounts[0]);
                        entity.setBadPlays(playCounts[1]);
                        matchRepository.save(entity);
                    } catch (Exception e) {
                        System.err.println("Error recalculating plays from JSON: " + e.getMessage());
                        dto.setGoodPlays(calculateGoodPlays(totalKills));
                        dto.setBadPlays(calculateBadPlays(totalKills));
                    }
                } else {
                    dto.setGoodPlays(calculateGoodPlays(totalKills));
                    dto.setBadPlays(calculateBadPlays(totalKills));
                }
            }
            
            dto.setDuration(calculateDuration(totalKills));
            dto.setScore(calculateUnifiedScore(totalKills, calculateDeaths(totalKills), 
                dto.getGoodPlays(), dto.getBadPlays()));
        } else {
            dto.setKills(0);
            dto.setDeaths(0);
            dto.setGoodPlays(0);
            dto.setBadPlays(0);
            dto.setDuration("00:00");
            dto.setScore(0.0);
        }
        
        dto.setDate(entity.getCreatedAt()); // Usar fecha y hora de creación real
        dto.setStatus(entity.getStatus()); // Incluir estado del match
        return dto;
    }
    
    private MatchDto convertToDtoForUser(MatchEntity entity, String user) {
        MatchDto dto = new MatchDto();
        dto.setId(entity.getMatchId());
        dto.setFileName(entity.getFileName());
        dto.setHasVideo(entity.getHasVideo());
        dto.setMap(entity.getMapName());
        dto.setGameType("Ranked"); // Default value
        
        // Calcular estadísticas específicas del usuario
        Long userKills = killRepository.countKillsByUserAndMatchId(user, entity.getMatchId());
        Long userDeaths = killRepository.countDeathsByUserAndMatchId(user, entity.getMatchId());
        
        // Debug: verificar que los valores no sean null
        if (userKills == null) userKills = 0L;
        if (userDeaths == null) userDeaths = 0L;
        
        dto.setKills(userKills.intValue());
        dto.setDeaths(userDeaths.intValue());
        
        // Calcular good/bad plays basado en los kills del usuario
        // Usar valores guardados del match si están disponibles (proporción del usuario)
        if (entity.getGoodPlays() != null && entity.getBadPlays() != null && entity.getTotalKills() != null && entity.getTotalKills() > 0) {
            // Calcular proporción del usuario basada en los valores reales del match
            double matchGoodRatio = (double) entity.getGoodPlays() / entity.getTotalKills();
            double matchBadRatio = (double) entity.getBadPlays() / entity.getTotalKills();
            dto.setGoodPlays((int)(userKills.intValue() * matchGoodRatio));
            dto.setBadPlays((int)(userKills.intValue() * matchBadRatio));
        } else {
            // Fallback a cálculo determinístico si no hay valores guardados
            dto.setGoodPlays(calculateGoodPlays(userKills.intValue()));
            dto.setBadPlays(calculateBadPlays(userKills.intValue()));
        }
        
        // Para duración y score, usar los valores del match completo
        Integer totalKills = entity.getTotalKills();
        if (totalKills != null) {
            dto.setDuration(calculateDuration(totalKills));
            // Usar fórmula unificada para score del usuario
            dto.setScore(calculateUnifiedScore(userKills.intValue(), userDeaths.intValue(), 
                dto.getGoodPlays(), dto.getBadPlays()));
        } else {
            dto.setDuration("00:00");
            dto.setScore(0.0);
        }
        
        dto.setDate(entity.getCreatedAt()); // Usar fecha y hora de creación real
        dto.setStatus(entity.getStatus()); // Incluir estado del match
        return dto;
    }
    
    /**
     * Calcula goodPlays y badPlays desde las predicciones del modelo ML.
     * El modelo ML real devuelve attacker_strengths (objeto con fortalezas detectadas).
     * Si attacker_strengths tiene alguna clave (precise, good_peek, good_positioning, good_decision)
     * entonces es una buena jugada.
     */
    private int[] calculatePlaysFromPredictions(Map<String, Object> mlResponse) {
        int goodPlays = 0;
        int badPlays = 0;
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) mlResponse.get("predictions");
        
        if (predictions != null) {
            for (Map<String, Object> prediction : predictions) {
                boolean isGoodPlay = false;
                
                // El modelo ML real devuelve attacker_strengths con las fortalezas detectadas
                @SuppressWarnings("unchecked")
                Map<String, Object> attackerStrengths = (Map<String, Object>) prediction.get("attacker_strengths");
                
                if (attackerStrengths != null && !attackerStrengths.isEmpty()) {
                    // Si tiene alguna fortaleza detectada (precise, good_peek, good_positioning, good_decision)
                    // es una buena jugada
                    isGoodPlay = true;
                }
                
                if (isGoodPlay) {
                    goodPlays++;
                } else {
                    badPlays++;
                }
            }
        }
        
        return new int[]{goodPlays, badPlays};
    }
    
    private int calculateDeaths(int kills) {
        double seed = kills * 0.98765;
        double factor = 0.7 + (seed % 0.2);
        return (int)(kills * factor);
    }
    
    private int calculateGoodPlays(int kills) {
        double seed = kills * 0.12345;
        double factor = 0.3 + (seed % 0.2);
        return (int)(kills * factor);
    }
    
    private int calculateBadPlays(int kills) {
        double seed = kills * 0.67890;
        double factor = 0.1 + (seed % 0.1);
        return (int)(kills * factor);
    }
    
    private String calculateDuration(int kills) {
        double seed = kills * 0.54321;
        int variance = (int)(seed % 300);
        int totalSeconds = kills * 90 + variance;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private double calculateUnifiedScore(int kills, int deaths, int goodPlays, int badPlays) {
        double kdr = deaths > 0 ? (double) kills / deaths : kills;
        
        double kdrComponent = Math.min(kdr * 1.5, 6.0);
        kdrComponent = Math.max(kdrComponent, 0.0);
        
        double playsComponent = 0.0;
        if (kills > 0) {
            double goodPlayRatio = (double) goodPlays / kills;
            double badPlayRatio = (double) badPlays / kills;
            
            double playDifference = goodPlayRatio - badPlayRatio;
            playsComponent = Math.min(playDifference * 8.0, 8.0);
            playsComponent = Math.max(playsComponent, -2.0);
        }
        
        double rawScore = (playsComponent * 2.0/3.0) + (kdrComponent * 1.0/3.0);
        double adjustedScore = rawScore + 5.0;
        return Math.min(Math.max(adjustedScore, 1.0), 10.0);
    }
    
    private void updateUserStatsFromKills(List<KillEntity> killEntities) {
        Set<String> usersInMatch = new HashSet<>();
        
        for (KillEntity kill : killEntities) {
            if (kill.getAttacker() != null) {
                usersInMatch.add(kill.getAttacker());
            }
            if (kill.getVictim() != null) {
                usersInMatch.add(kill.getVictim());
            }
        }
        
        usersInMatch.forEach(this::updateGlobalUserStatsFromDatabase);
    }
    
    private void updateGlobalUserStatsFromDatabase(String userName) {
        try {
            Long totalKillsFromDB = killRepository.countKillsByUser(userName);
            Long totalDeathsFromDB = killRepository.countDeathsByUser(userName);
            
            int actualKills = (totalKillsFromDB != null) ? totalKillsFromDB.intValue() : 0;
            int actualDeaths = (totalDeathsFromDB != null) ? totalDeathsFromDB.intValue() : 0;
            
            int totalMatches = calculateUserMatches(userName);
            
            int goodPlays = calculateGoodPlays(actualKills);
            int badPlays = calculateBadPlays(actualKills);
            double score = calculateUnifiedScore(actualKills, actualDeaths, goodPlays, badPlays);
            
            userService.updateUserStatsWithMatches(userName, actualKills, actualDeaths, score, totalMatches);
        } catch (Exception e) {
            System.err.println("Error updating global stats for " + userName + ": " + e.getMessage());
        }
    }
    
    private int calculateUserMatches(String userName) {
        try {
            List<String> matchIds = killRepository.findDistinctMatchIdsByUser(userName);
            return matchIds != null ? matchIds.size() : 0;
        } catch (Exception e) {
            System.err.println("Error calculating matches for " + userName + ": " + e.getMessage());
            return 0;
        }
    }
}