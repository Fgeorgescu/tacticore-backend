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
     * Actualiza un match existente con resultados y guarda todos sus kills
     */
    @Transactional
    public void updateMatchWithKills(String matchId, Integer totalKills, Integer tickrate, String mapName, List<KillEntity> killEntities) {
        // Buscar el match existente
        Optional<MatchEntity> matchOpt = matchRepository.findByMatchId(matchId);
        if (matchOpt.isPresent()) {
            MatchEntity match = matchOpt.get();
            
            // Actualizar datos del match
            match.setTotalKills(totalKills);
            match.setTickrate(tickrate);
            match.setMapName(mapName);
            match.setStatus("completed");
            
            // Guardar el match actualizado
            matchRepository.save(match);
            
            // Guardar todos los kills
            if (killEntities != null && !killEntities.isEmpty()) {
                killRepository.saveAll(killEntities);
                
                // Calcular y actualizar estadísticas de usuarios
                updateUserStatsFromKills(killEntities);
            }
            
            System.out.println("Actualizado match " + matchId + " con " + 
                              (killEntities != null ? killEntities.size() : 0) + " kills");
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
            // Podríamos agregar un campo errorMessage a la entidad si es necesario
            matchRepository.save(match);
        });
    }
    
    public String getMatchStatus(String matchId) {
        return matchRepository.findByMatchId(matchId)
                .map(MatchEntity::getStatus)
                .orElse("not_found");
    }
    
    public List<MatchDto> getMatchesByUser(String user) {
        // Obtener todos los matches que contienen kills del usuario
        List<MatchEntity> entities = matchRepository.findAll();
        return entities.stream()
                .filter(match -> {
                    // Verificar si el match tiene kills del usuario
                    return killRepository.existsByMatchIdAndAttackerName(match.getMatchId(), user);
                })
                .map(entity -> convertToDtoForUser(entity, user))
                .collect(Collectors.toList());
    }
    
    private MatchDto convertToDto(MatchEntity entity) {
        MatchDto dto = new MatchDto();
        dto.setId(entity.getMatchId());
        dto.setFileName(entity.getFileName());
        dto.setHasVideo(entity.getHasVideo());
        dto.setMap(entity.getMapName());
        dto.setGameType("Ranked"); // Default value
        
        // Manejar valores nulos para matches en procesamiento
        Integer totalKills = entity.getTotalKills();
        if (totalKills != null) {
            dto.setKills(totalKills);
            dto.setDeaths(calculateDeaths(totalKills));
            dto.setGoodPlays(calculateGoodPlays(totalKills));
            dto.setBadPlays(calculateBadPlays(totalKills));
            dto.setDuration(calculateDuration(totalKills));
            // Usar fórmula unificada para score general
            dto.setScore(calculateUnifiedScore(totalKills, calculateDeaths(totalKills), 
                calculateGoodPlays(totalKills), calculateBadPlays(totalKills)));
        } else {
            // Valores por defecto para matches en procesamiento
            dto.setKills(0);
            dto.setDeaths(0);
            dto.setGoodPlays(0);
            dto.setBadPlays(0);
            dto.setDuration("00:00");
            dto.setScore(0.0);
        }
        
        dto.setDate(entity.getCreatedAt()); // Usar fecha y hora de creación real
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
        dto.setGoodPlays(calculateGoodPlays(userKills.intValue()));
        dto.setBadPlays(calculateBadPlays(userKills.intValue()));
        
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
        return dto;
    }
    
    private int calculateDeaths(int kills) {
        // Deterministic calculation: deaths are usually 70-90% of kills
        // Use kills as seed for consistent results
        double seed = kills * 0.98765; // Deterministic seed
        double factor = 0.7 + (seed % 0.2); // 70-90% range
        return (int)(kills * factor);
    }
    
    private int calculateGoodPlays(int kills) {
        // Deterministic calculation: good plays are 30-50% of kills
        // Use kills as seed for consistent results
        double seed = kills * 0.12345; // Deterministic seed
        double factor = 0.3 + (seed % 0.2); // 30-50% range
        return (int)(kills * factor);
    }
    
    private int calculateBadPlays(int kills) {
        // Deterministic calculation: bad plays are 10-20% of kills
        // Use kills as seed for consistent results
        double seed = kills * 0.67890; // Different seed for variety
        double factor = 0.1 + (seed % 0.1); // 10-20% range
        return (int)(kills * factor);
    }
    
    private String calculateDuration(int kills) {
        // Deterministic calculation: duration based on kills (roughly 1.5 minutes per kill)
        // Use kills as seed for consistent results
        double seed = kills * 0.54321; // Deterministic seed
        int variance = (int)(seed % 300); // 0-300 seconds variance
        int totalSeconds = kills * 90 + variance;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private double calculateUnifiedScore(int kills, int deaths, int goodPlays, int badPlays) {
        // Fórmula unificada que incluye KDR y jugadas buenas/malas
        // Ponderación: 2/3 jugadas, 1/3 KDR
        
        // Calcular KDR
        double kdr = deaths > 0 ? (double) kills / deaths : kills;
        
        // Componente KDR (1/3 del peso total)
        double kdrComponent = Math.min(kdr * 1.5, 6.0); // Max 6 puntos por KDR
        kdrComponent = Math.max(kdrComponent, 0.0); // Mínimo 0
        
        // Componente de jugadas (2/3 del peso total)
        double playsComponent = 0.0;
        if (kills > 0) {
            // Ratio de buenas jugadas vs total de actividad
            double goodPlayRatio = (double) goodPlays / kills;
            double badPlayRatio = (double) badPlays / kills;
            
            // Score basado en la diferencia entre buenas y malas jugadas
            double playDifference = goodPlayRatio - badPlayRatio;
            playsComponent = Math.min(playDifference * 8.0, 8.0); // Max 8 puntos
            playsComponent = Math.max(playsComponent, -2.0); // Mínimo -2 puntos
        }
        
        // Combinar componentes con ponderación
        double rawScore = (playsComponent * 2.0/3.0) + (kdrComponent * 1.0/3.0);
        
        // Ajustar al rango 1.0-10.0
        double adjustedScore = rawScore + 5.0; // Centrar en 5.0
        return Math.min(Math.max(adjustedScore, 1.0), 10.0);
    }
    
    /**
     * Actualiza las estadísticas de usuarios basándose en TODOS los kills de la base de datos (Enfoque B)
     * Este método recalcula las estadísticas globales para garantizar consistencia
     */
    private void updateUserStatsFromKills(List<KillEntity> killEntities) {
        // Obtener todos los usuarios únicos que participaron en esta partida
        Set<String> usersInMatch = new HashSet<>();
        
        for (KillEntity kill : killEntities) {
            if (kill.getAttacker() != null) {
                usersInMatch.add(kill.getAttacker());
            }
            if (kill.getVictim() != null) {
                usersInMatch.add(kill.getVictim());
            }
        }
        
        // Para cada usuario, recalcular sus estadísticas globales desde la base de datos
        for (String userName : usersInMatch) {
            updateGlobalUserStatsFromDatabase(userName);
        }
    }
    
    /**
     * Recalcula las estadísticas globales de un usuario desde toda la base de datos
     */
    private void updateGlobalUserStatsFromDatabase(String userName) {
        try {
            // Obtener estadísticas reales de kills desde toda la base de datos
            Long totalKillsFromDB = killRepository.countKillsByUser(userName);
            Long totalDeathsFromDB = killRepository.countDeathsByUser(userName);
            
            int actualKills = (totalKillsFromDB != null) ? totalKillsFromDB.intValue() : 0;
            int actualDeaths = (totalDeathsFromDB != null) ? totalDeathsFromDB.intValue() : 0;
            
            // Calcular cuántas partidas diferentes ha jugado este usuario
            int totalMatches = calculateUserMatches(userName);
            
            // Calcular estadísticas usando los métodos del sistema
            int goodPlays = calculateGoodPlays(actualKills);
            int badPlays = calculateBadPlays(actualKills);
            double score = calculateUnifiedScore(actualKills, actualDeaths, goodPlays, badPlays);
            
            // Actualizar el usuario con estadísticas globales
            userService.updateUserStatsWithMatches(userName, actualKills, actualDeaths, score, totalMatches);
            
            System.out.println("Actualizadas estadísticas globales para " + userName + 
                              ": " + actualKills + " kills, " + actualDeaths + " deaths, " + totalMatches + " matches, score: " + String.format("%.2f", score));
                              
        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas globales para " + userName + ": " + e.getMessage());
        }
    }
    
    /**
     * Calcula cuántas partidas diferentes ha jugado un usuario
     */
    private int calculateUserMatches(String userName) {
        try {
            // Obtener todos los matchIds únicos donde aparece este usuario
            List<String> matchIds = killRepository.findDistinctMatchIdsByUser(userName);
            int matches = matchIds != null ? matchIds.size() : 0;
            return matches;
        } catch (Exception e) {
            System.err.println("Error calculando matches para " + userName + ": " + e.getMessage());
            return 0;
        }
    }
}