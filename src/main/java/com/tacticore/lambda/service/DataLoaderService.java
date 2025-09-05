package com.tacticore.lambda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.KillPredictionEntity;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.repository.KillPredictionRepository;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DataLoaderService {
    
    @Autowired
    private KillRepository killRepository;
    
    @Autowired
    private KillPredictionRepository killPredictionRepository;
    
    @Autowired
    private MatchRepository matchRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void loadDataFromJson(String filePath) throws IOException {
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        
        // Cargar información del match
        String mapName = rootNode.path("map").asText("Unknown");
        Integer tickrate = rootNode.path("tickrate").asInt(64);
        Integer totalKills = rootNode.path("total_kills").asInt(0);
        
        MatchEntity match = new MatchEntity(
            "example_match",
            "example.json",
            mapName,
            tickrate,
            totalKills,
            "processed",
            false
        );
        
        if (!matchRepository.existsByMatchId("example_match")) {
            matchRepository.save(match);
        }
        
        // Cargar kills y predicciones
        JsonNode predictionsNode = rootNode.path("predictions");
        if (predictionsNode.isArray()) {
            for (JsonNode predictionNode : predictionsNode) {
                loadKillFromJson(predictionNode);
            }
        }
        
        System.out.println("Datos cargados exitosamente: " + totalKills + " kills");
    }
    
    private void loadKillFromJson(JsonNode killNode) {
        try {
            // Crear entidad Kill
            KillEntity kill = new KillEntity();
            kill.setKillId(killNode.path("kill_id").asText());
            kill.setAttacker(killNode.path("attacker").asText());
            kill.setVictim(killNode.path("victim").asText());
            kill.setPlace(killNode.path("place").asText());
            kill.setRound(killNode.path("round").asInt());
            kill.setWeapon(killNode.path("weapon").asText());
            kill.setHeadshot(killNode.path("headshot").asBoolean());
            kill.setDistance(killNode.path("distance").asDouble());
            kill.setTimeInRound(killNode.path("time_in_round").asDouble());
            
            // Información del contexto
            JsonNode contextNode = killNode.path("context");
            if (!contextNode.isMissingNode()) {
                kill.setKillTick(contextNode.path("kill_tick").asLong());
                kill.setSide(contextNode.path("side").asText());
                kill.setAttackerX(contextNode.path("attacker_x").asDouble());
                kill.setAttackerY(contextNode.path("attacker_y").asDouble());
                kill.setAttackerZ(contextNode.path("attacker_z").asDouble());
                kill.setVictimX(contextNode.path("victim_x").asDouble());
                kill.setVictimY(contextNode.path("victim_y").asDouble());
                kill.setVictimZ(contextNode.path("victim_z").asDouble());
                kill.setAttackerHealth(contextNode.path("attacker_health").asDouble());
                kill.setVictimHealth(contextNode.path("victim_health").asDouble());
                kill.setFlashNear(contextNode.path("flash_near").asBoolean());
                kill.setSmokeNear(contextNode.path("smoke_near").asBoolean());
                kill.setMolotovNear(contextNode.path("molotov_near").asBoolean());
                kill.setHeNear(contextNode.path("he_near").asBoolean());
            }
            
            // Guardar kill si no existe
            if (!killRepository.findByKillId(kill.getKillId()).isPresent()) {
                killRepository.save(kill);
            }
            
            // Cargar predicciones
            JsonNode predictionNode = killNode.path("prediction");
            if (!predictionNode.isMissingNode()) {
                loadPredictionsFromJson(kill.getKillId(), predictionNode);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando kill: " + e.getMessage());
        }
    }
    
    private void loadPredictionsFromJson(String killId, JsonNode predictionNode) {
        try {
            // Top predictions
            JsonNode topPredictionsNode = predictionNode.path("top_predictions");
            if (topPredictionsNode.isArray()) {
                for (JsonNode topPredNode : topPredictionsNode) {
                    KillPredictionEntity prediction = new KillPredictionEntity();
                    prediction.setKillId(killId);
                    prediction.setLabel(topPredNode.path("label").asText());
                    prediction.setConfidence(topPredNode.path("confidence").asDouble());
                    prediction.setIsTopPrediction(true);
                    
                    killPredictionRepository.save(prediction);
                }
            }
            
            // All probabilities
            JsonNode allProbabilitiesNode = predictionNode.path("all_probabilities");
            if (allProbabilitiesNode.isObject()) {
                allProbabilitiesNode.fields().forEachRemaining(entry -> {
                    KillPredictionEntity prediction = new KillPredictionEntity();
                    prediction.setKillId(killId);
                    prediction.setLabel(entry.getKey());
                    prediction.setConfidence(entry.getValue().asDouble());
                    prediction.setIsTopPrediction(false);
                    
                    killPredictionRepository.save(prediction);
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando predicciones para kill " + killId + ": " + e.getMessage());
        }
    }
    
    public void clearAllData() {
        killPredictionRepository.deleteAll();
        killRepository.deleteAll();
        matchRepository.deleteAll();
        System.out.println("Todos los datos han sido eliminados");
    }
}
