package com.tacticore.lambda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Servicio para leer datos de partidas directamente desde archivos JSON
 * Mantiene el formato completo con coordenadas de imagen del modelo ML
 */
@Service
public class JsonMatchService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Mapeo de matchIds a archivos JSON
    // Puedes extender este mapa para incluir más partidas
    private static final Map<String, String> MATCH_JSON_MAP = new HashMap<>();
    
    static {
        // Mapeo por matchId numérico o nombre de partida
        // IDs numéricos comunes
        MATCH_JSON_MAP.put("1", "demos-jsons/mirage1.json");
        MATCH_JSON_MAP.put("2", "demos-jsons/inferno1.json");
        MATCH_JSON_MAP.put("3", "demos-jsons/dust1.json");
        
        // Nombres de partidas precargadas
        MATCH_JSON_MAP.put("mirage_demo", "demos-jsons/de_mirage.json");
        MATCH_JSON_MAP.put("mirage1_demo", "demos-jsons/mirage1.json");
        MATCH_JSON_MAP.put("dust2_demo", "demos-jsons/dust1.json");
        MATCH_JSON_MAP.put("inferno_demo", "demos-jsons/inferno1.json");
        MATCH_JSON_MAP.put("mirage2_demo", "demos-jsons/mirage1.json");
        
        // También mapear por nombre de archivo sin extensión
        MATCH_JSON_MAP.put("mirage1", "demos-jsons/mirage1.json");
        MATCH_JSON_MAP.put("inferno1", "demos-jsons/inferno1.json");
        MATCH_JSON_MAP.put("dust1", "demos-jsons/dust1.json");
        MATCH_JSON_MAP.put("de_mirage", "demos-jsons/de_mirage.json");
        MATCH_JSON_MAP.put("de_nuke", "demos-jsons/de_nuke.json");
        MATCH_JSON_MAP.put("nuke", "demos-jsons/de_nuke.json");
        MATCH_JSON_MAP.put("nuke1", "demos-jsons/de_nuke.json");
        
        // Agregar más mappings según sea necesario
    }
    
    /**
     * Obtiene el archivo JSON asociado a un matchId
     */
    private String getJsonPathForMatch(String matchId) {
        String jsonPath = MATCH_JSON_MAP.get(matchId);
        if (jsonPath != null) {
            return jsonPath;
        }
        
        String[] searchPaths = {"demos-jsons", "../PFI-2025/demos-jsons", "./demos-jsons"};
        
        for (String searchPath : searchPaths) {
            File jsonDir = new File(searchPath);
            if (jsonDir.exists() && jsonDir.isDirectory()) {
                File[] jsonFiles = jsonDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (jsonFiles != null) {
                    String matchIdLower = matchId.toLowerCase();
                    for (File jsonFile : jsonFiles) {
                        String fileName = jsonFile.getName().toLowerCase().replace(".json", "");
                        if (fileName.contains(matchIdLower) || matchIdLower.contains(fileName) || 
                            fileName.equals(matchIdLower) || matchIdLower.equals(fileName)) {
                            return jsonFile.getPath();
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public String getJsonPathForMatchByFileName(String matchId, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return getJsonPathForMatch(matchId);
        }
        
        String baseName = fileName;
        if (baseName.contains(".")) {
            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
        }
        String baseNameLower = baseName.toLowerCase();
        
        String[] jsonFileVariations = {
            baseName + ".json",
            "de_" + baseName + ".json",
            baseName + "1.json",
            "de_" + baseName + "1.json"
        };
        
        String[] searchPaths = {"demos-jsons", "../PFI-2025/demos-jsons", "./demos-jsons"};
        
        for (String searchPath : searchPaths) {
            for (String jsonFileName : jsonFileVariations) {
                File jsonFile = new File(searchPath, jsonFileName);
                if (jsonFile.exists()) {
                    return jsonFile.getPath();
                }
            }
        }
        
        for (String searchPath : searchPaths) {
            File jsonDir = new File(searchPath);
            if (jsonDir.exists() && jsonDir.isDirectory()) {
                File[] jsonFiles = jsonDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (jsonFiles != null) {
                    for (File jsonFile : jsonFiles) {
                        String jsonFileName = jsonFile.getName().toLowerCase().replace(".json", "");
                        if (jsonFileName.contains(baseNameLower) || baseNameLower.contains(jsonFileName) ||
                            jsonFileName.equals(baseNameLower)) {
                            return jsonFile.getPath();
                        }
                    }
                }
            }
        }
        
        return getJsonPathForMatch(matchId);
    }
    
    public Map<String, Object> getMatchDataFromJson(String matchId) {
        String jsonPath = getJsonPathForMatch(matchId);
        
        if (jsonPath == null) {
            return null;
        }
        
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            return null;
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.convertValue(rootNode, Map.class);
            
            return result;
        } catch (IOException e) {
            System.err.println("Error reading JSON file " + jsonPath + ": " + e.getMessage());
            return null;
        }
    }
    
    public Map<String, Object> getMatchKillsFromJson(String matchId, String user) {
        Map<String, Object> fullData = getMatchDataFromJson(matchId);
        if (fullData == null) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) fullData.get("predictions");
        if (predictions == null) {
            return fullData;
        }
        
        // Transformar predicciones al formato del frontend
        List<Map<String, Object>> transformedKills = new ArrayList<>();
        Map<Integer, Map<String, Integer>> roundTeamCounts = new HashMap<>();
            
            for (Map<String, Object> prediction : predictions) {
            String attacker = getNestedString(prediction, "attacker");
                String attackerName = getNestedString(prediction, "context", "attacker_name");
                String victim = getNestedString(prediction, "victim");
                String victimName = getNestedString(prediction, "context", "victim_name");
                
                String actualAttacker = attackerName != null ? attackerName : attacker;
                String actualVictim = victimName != null ? victimName : victim;
                
            // Filtrar por usuario si se especifica
            if (user != null && !user.isEmpty()) {
                if (!user.equalsIgnoreCase(actualAttacker) && !user.equalsIgnoreCase(actualVictim)) {
                    continue;
                }
            }
            
            Map<String, Object> kill = transformPredictionToKill(prediction, roundTeamCounts);
            transformedKills.add(kill);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("kills", transformedKills);
        result.put("matchId", matchId);
        result.put("total_kills", transformedKills.size());
        result.put("map", fullData.get("map"));
        if (user != null) {
            result.put("filteredBy", user);
        }
        
        return result;
    }
    
    /**
     * Transforma una predicción del modelo ML al formato esperado por el frontend
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> transformPredictionToKill(Map<String, Object> prediction, Map<Integer, Map<String, Integer>> roundTeamCounts) {
        Map<String, Object> context = (Map<String, Object>) prediction.get("context");
        Map<String, Object> predictionResult = (Map<String, Object>) prediction.get("prediction");
        
        Map<String, Object> kill = new HashMap<>();
        
        // Campos básicos
        kill.put("id", prediction.get("kill_id"));
        kill.put("killer", getNestedString(prediction, "context", "attacker_name"));
        kill.put("victim", getNestedString(prediction, "context", "victim_name"));
        kill.put("weapon", prediction.get("weapon"));
        kill.put("round", prediction.get("round"));
        kill.put("position", context != null ? context.get("place") : null);
        kill.put("attackerSide", context != null ? context.get("side") : null);
        
        // Tiempo formateado
        Object timeInRound = prediction.get("time_in_round");
        if (timeInRound != null) {
            double time = timeInRound instanceof Number ? ((Number) timeInRound).doubleValue() : 0.0;
            kill.put("time", String.format("%.1fs", time));
        }
        
        // Determinar si es buena jugada basado en la predicción
        // Usa lógica compartida con PreloadedDataService y DatabaseMatchService
        String predictedLabel = null;
        if (predictionResult != null) {
            predictedLabel = (String) predictionResult.get("predicted_label");
        }
        boolean isGoodPlay = PreloadedDataService.isGoodPlayFromLabel(predictedLabel);
        kill.put("isGoodPlay", isGoodPlay);
        
        // Team alive - calcular basado en la ronda
        Integer round = (Integer) prediction.get("round");
        if (round != null && context != null) {
            roundTeamCounts.putIfAbsent(round, new HashMap<>());
            Map<String, Integer> teamCounts = roundTeamCounts.get(round);
            
            Object playersAliveT = context.get("players_alive_t");
            Object playersAliveCT = context.get("players_alive_ct");
            
            int tAlive = playersAliveT instanceof Number ? ((Number) playersAliveT).intValue() : 5;
            int ctAlive = playersAliveCT instanceof Number ? ((Number) playersAliveCT).intValue() : 5;
            
            teamCounts.put("ct", ctAlive);
            teamCounts.put("t", tAlive);
            kill.put("teamAlive", new HashMap<>(teamCounts));
        }
        
        // Coordenadas del atacante (posición en el juego)
        if (context != null) {
            Object attackerX = context.get("attacker_x");
            Object attackerY = context.get("attacker_y");
            Object attackerZ = context.get("attacker_z");
            
            if (attackerX != null && attackerY != null) {
                Map<String, Double> attackerPosition = new HashMap<>();
                attackerPosition.put("x", ((Number) attackerX).doubleValue());
                attackerPosition.put("y", ((Number) attackerY).doubleValue());
                attackerPosition.put("z", attackerZ != null ? ((Number) attackerZ).doubleValue() : 0.0);
                kill.put("attackerPosition", attackerPosition);
            }
            
            // Coordenadas de imagen del atacante
            Object attackerImageX = context.get("attacker_image_x");
            Object attackerImageY = context.get("attacker_image_y");
            if (attackerImageX != null && attackerImageY != null) {
                Map<String, Integer> attackerImagePosition = new HashMap<>();
                attackerImagePosition.put("x", ((Number) attackerImageX).intValue());
                attackerImagePosition.put("y", ((Number) attackerImageY).intValue());
                kill.put("attackerImagePosition", attackerImagePosition);
            }
            
            // Coordenadas de la víctima (posición en el juego)
            Object victimX = context.get("victim_x");
            Object victimY = context.get("victim_y");
            Object victimZ = context.get("victim_z");
            
            if (victimX != null && victimY != null) {
                Map<String, Double> victimPosition = new HashMap<>();
                victimPosition.put("x", ((Number) victimX).doubleValue());
                victimPosition.put("y", ((Number) victimY).doubleValue());
                victimPosition.put("z", victimZ != null ? ((Number) victimZ).doubleValue() : 0.0);
                kill.put("victimPosition", victimPosition);
            }
            
            // Coordenadas de imagen de la víctima
            Object victimImageX = context.get("victim_image_x");
            Object victimImageY = context.get("victim_image_y");
            if (victimImageX != null && victimImageY != null) {
                Map<String, Integer> victimImagePosition = new HashMap<>();
                victimImagePosition.put("x", ((Number) victimImageX).intValue());
                victimImagePosition.put("y", ((Number) victimImageY).intValue());
                kill.put("victimImagePosition", victimImagePosition);
            }
        }
        
        return kill;
    }
    
    public Map<String, Object> getMatchKillsFromJsonByFileName(String matchId, String fileName, String user) {
        String jsonPath = getJsonPathForMatchByFileName(matchId, fileName);
        
        if (jsonPath == null) {
            return null;
        }
        
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            return null;
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> fullData = objectMapper.convertValue(rootNode, Map.class);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> predictions = (List<Map<String, Object>>) fullData.get("predictions");
            if (predictions == null) {
                return fullData;
            }
            
            if (user != null && !user.isEmpty()) {
                List<Map<String, Object>> filteredPredictions = new ArrayList<>();
                
                for (Map<String, Object> prediction : predictions) {
                    String attacker = getNestedString(prediction, "attacker");
                    String attackerName = getNestedString(prediction, "context", "attacker_name");
                    String victim = getNestedString(prediction, "victim");
                    String victimName = getNestedString(prediction, "context", "victim_name");
                    
                    String actualAttacker = attackerName != null ? attackerName : attacker;
                    String actualVictim = victimName != null ? victimName : victim;
                    
                    if (user.equalsIgnoreCase(actualAttacker) || user.equalsIgnoreCase(actualVictim)) {
                        filteredPredictions.add(prediction);
                    }
                }
                
                Map<String, Object> filteredData = new HashMap<>(fullData);
                filteredData.put("predictions", filteredPredictions);
                filteredData.put("total_kills", filteredPredictions.size());
                
                return filteredData;
            }
            
            return fullData;
        } catch (IOException e) {
            System.err.println("Error reading JSON file " + jsonPath + ": " + e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private String getNestedString(Map<String, Object> map, String... keys) {
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return current != null ? current.toString() : null;
    }
    
}

