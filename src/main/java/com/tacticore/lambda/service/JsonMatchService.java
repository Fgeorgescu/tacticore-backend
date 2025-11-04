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

