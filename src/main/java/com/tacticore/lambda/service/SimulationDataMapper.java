package com.tacticore.lambda.service;

import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.MatchEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SimulationDataMapper {
    
    /**
     * Mapea la respuesta del ML service a entidades de base de datos
     * @param mlResponse Respuesta del servicio ML
     * @param matchId ID del match existente
     * @param fileName Nombre del archivo
     * @return Resultado con MatchEntity actualizado y lista de KillEntity
     */
    public SimulationResult mapMLResponseToEntities(Map<String, Object> mlResponse, String matchId, String fileName) {
        try {
            // Extraer datos básicos del match
            Integer totalKills = (Integer) mlResponse.get("total_kills");
            String mapName = (String) mlResponse.get("map");
            Integer tickrate = (Integer) mlResponse.get("tickrate");
            
            // NO crear nuevo MatchEntity, solo retornar los datos para actualizar el existente
            MatchEntity matchEntity = null; // No creamos nuevo match, solo actualizamos el existente
            
            // Mapear kills
            List<KillEntity> killEntities = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> predictions = (List<Map<String, Object>>) mlResponse.get("predictions");
            
            if (predictions != null) {
                for (Map<String, Object> prediction : predictions) {
                    KillEntity killEntity = mapPredictionToKillEntity(prediction);
                    killEntity.setMatchId(matchId); // Asignar el matchId al kill
                    killEntities.add(killEntity);
                }
            }
            
            return new SimulationResult(matchEntity, killEntities, totalKills, tickrate, mapName);
            
        } catch (Exception e) {
            throw new RuntimeException("Error mapeando respuesta ML a entidades: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mapea una predicción individual a KillEntity
     */
    private KillEntity mapPredictionToKillEntity(Map<String, Object> prediction) {
        KillEntity killEntity = new KillEntity();
        
        // Datos básicos del kill
        killEntity.setKillId((String) prediction.get("kill_id"));
        killEntity.setAttacker((String) prediction.get("attacker"));
        killEntity.setVictim((String) prediction.get("victim"));
        killEntity.setPlace((String) prediction.get("place"));
        killEntity.setRound((Integer) prediction.get("round"));
        killEntity.setWeapon((String) prediction.get("weapon"));
        killEntity.setHeadshot((Boolean) prediction.get("headshot"));
        killEntity.setDistance(((Number) prediction.get("distance")).doubleValue());
        killEntity.setTimeInRound(((Number) prediction.get("time_in_round")).doubleValue());
        
        // Extraer contexto si existe
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) prediction.get("context");
        if (context != null) {
            killEntity.setKillTick(((Number) context.get("kill_tick")).longValue());
            killEntity.setSide((String) context.get("side"));
            
            // Posiciones del atacante
            killEntity.setAttackerX(((Number) context.get("attacker_x")).doubleValue());
            killEntity.setAttackerY(((Number) context.get("attacker_y")).doubleValue());
            killEntity.setAttackerZ(((Number) context.get("attacker_z")).doubleValue());
            
            // Posiciones de la víctima
            killEntity.setVictimX(((Number) context.get("victim_x")).doubleValue());
            killEntity.setVictimY(((Number) context.get("victim_y")).doubleValue());
            killEntity.setVictimZ(((Number) context.get("victim_z")).doubleValue());
            
            // Salud
            killEntity.setAttackerHealth(((Number) context.get("attacker_health")).doubleValue());
            killEntity.setVictimHealth(((Number) context.get("victim_health")).doubleValue());
            
            // Utilidades cercanas
            killEntity.setFlashNear((Boolean) context.get("flash_near"));
            killEntity.setSmokeNear((Boolean) context.get("smoke_near"));
            killEntity.setMolotovNear((Boolean) context.get("molotov_near"));
            killEntity.setHeNear((Boolean) context.get("he_near"));
        }
        
        return killEntity;
    }
    
    /**
     * Clase para encapsular el resultado del mapeo
     */
    public static class SimulationResult {
        private final MatchEntity matchEntity;
        private final List<KillEntity> killEntities;
        private final Integer totalKills;
        private final Integer tickrate;
        private final String mapName;
        
        public SimulationResult(MatchEntity matchEntity, List<KillEntity> killEntities, 
                              Integer totalKills, Integer tickrate, String mapName) {
            this.matchEntity = matchEntity;
            this.killEntities = killEntities;
            this.totalKills = totalKills;
            this.tickrate = tickrate;
            this.mapName = mapName;
        }
        
        public MatchEntity getMatchEntity() {
            return matchEntity;
        }
        
        public List<KillEntity> getKillEntities() {
            return killEntities;
        }
        
        public Integer getTotalKills() {
            return totalKills;
        }
        
        public Integer getTickrate() {
            return tickrate;
        }
        
        public String getMapName() {
            return mapName;
        }
    }
}
