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
        // Primero buscar en el mapa directo
        String jsonPath = MATCH_JSON_MAP.get(matchId);
        if (jsonPath != null) {
            return jsonPath;
        }
        
        // Si no está en el mapa, intentar buscar por nombre de archivo del match
        // Buscar en el directorio demos-jsons
        String[] searchPaths = {"demos-jsons", "../PFI-2025/demos-jsons", "./demos-jsons"};
        
        for (String searchPath : searchPaths) {
            File jsonDir = new File(searchPath);
            if (jsonDir.exists() && jsonDir.isDirectory()) {
                File[] jsonFiles = jsonDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (jsonFiles != null) {
                    // Intentar encontrar un archivo que coincida con el matchId
                    String matchIdLower = matchId.toLowerCase();
                    for (File jsonFile : jsonFiles) {
                        String fileName = jsonFile.getName().toLowerCase().replace(".json", "");
                        // Comparar matchId con nombre de archivo
                        if (fileName.contains(matchIdLower) || matchIdLower.contains(fileName) || 
                            fileName.equals(matchIdLower) || matchIdLower.equals(fileName)) {
                            System.out.println("Encontrado archivo JSON por búsqueda: " + jsonFile.getPath());
                            return jsonFile.getPath();
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene el archivo JSON asociado a un matchId usando el nombre del archivo DEM
     * @param matchId ID del match
     * @param fileName Nombre del archivo DEM (ej: "nuke.dem")
     * @return Ruta al archivo JSON o null si no se encuentra
     */
    public String getJsonPathForMatchByFileName(String matchId, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return getJsonPathForMatch(matchId);
        }
        
        // Extraer nombre base del archivo (sin extensión)
        String baseName = fileName;
        if (baseName.contains(".")) {
            baseName = baseName.substring(0, baseName.lastIndexOf('.'));
        }
        String baseNameLower = baseName.toLowerCase();
        
        // Buscar archivo JSON con diferentes variaciones
        String[] jsonFileVariations = {
            baseName + ".json",                    // nuke.json
            "de_" + baseName + ".json",            // de_nuke.json
            baseName + "1.json",                   // nuke1.json
            "de_" + baseName + "1.json"            // de_nuke1.json
        };
        
        String[] searchPaths = {"demos-jsons", "../PFI-2025/demos-jsons", "./demos-jsons"};
        
        for (String searchPath : searchPaths) {
            for (String jsonFileName : jsonFileVariations) {
                File jsonFile = new File(searchPath, jsonFileName);
                if (jsonFile.exists()) {
                    System.out.println("Encontrado archivo JSON por fileName (" + fileName + "): " + jsonFile.getPath());
                    return jsonFile.getPath();
                }
            }
        }
        
        // Si no se encontró, intentar búsqueda por coincidencia parcial
        for (String searchPath : searchPaths) {
            File jsonDir = new File(searchPath);
            if (jsonDir.exists() && jsonDir.isDirectory()) {
                File[] jsonFiles = jsonDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (jsonFiles != null) {
                    for (File jsonFile : jsonFiles) {
                        String jsonFileName = jsonFile.getName().toLowerCase().replace(".json", "");
                        // Buscar coincidencia: si el nombre del JSON contiene el nombre del DEM o viceversa
                        if (jsonFileName.contains(baseNameLower) || baseNameLower.contains(jsonFileName) ||
                            jsonFileName.equals(baseNameLower)) {
                            System.out.println("Encontrado archivo JSON por coincidencia parcial (" + fileName + "): " + jsonFile.getPath());
                            return jsonFile.getPath();
                        }
                    }
                }
            }
        }
        
        // Si no se encontró por fileName, intentar por matchId
        return getJsonPathForMatch(matchId);
    }
    
    /**
     * Lee el archivo JSON completo para un matchId dado
     * Retorna null si el archivo no existe o no se puede leer
     */
    public Map<String, Object> getMatchDataFromJson(String matchId) {
        String jsonPath = getJsonPathForMatch(matchId);
        
        if (jsonPath == null) {
            System.out.println("No se encontró archivo JSON para matchId: " + matchId);
            return null;
        }
        
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            System.out.println("Archivo JSON no existe: " + jsonPath);
            return null;
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            
            // Convertir a Map manteniendo toda la estructura
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.convertValue(rootNode, Map.class);
            
            System.out.println("Cargado JSON para matchId " + matchId + " desde " + jsonPath + 
                             " con " + (result.containsKey("predictions") ? 
                             ((List<?>) result.get("predictions")).size() : 0) + " predictions");
            
            return result;
        } catch (IOException e) {
            System.err.println("Error leyendo archivo JSON " + jsonPath + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene solo los predictions filtrados por usuario (si se proporciona)
     */
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
        
        // Si se proporciona un usuario, filtrar predictions
        if (user != null && !user.isEmpty()) {
            List<Map<String, Object>> filteredPredictions = new ArrayList<>();
            
            for (Map<String, Object> prediction : predictions) {
                // Verificar si el usuario es el attacker o victim
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
            
            // Crear nuevo objeto con predictions filtradas
            Map<String, Object> filteredData = new HashMap<>(fullData);
            filteredData.put("predictions", filteredPredictions);
            filteredData.put("total_kills", filteredPredictions.size());
            
            System.out.println("Filtrados " + filteredPredictions.size() + " predictions para usuario " + user);
            return filteredData;
        }
        
        return fullData;
    }
    
    /**
     * Obtiene solo los predictions filtrados por usuario usando el nombre del archivo
     */
    public Map<String, Object> getMatchKillsFromJsonByFileName(String matchId, String fileName, String user) {
        String jsonPath = getJsonPathForMatchByFileName(matchId, fileName);
        
        if (jsonPath == null) {
            System.out.println("No se encontró archivo JSON para matchId: " + matchId + " con fileName: " + fileName);
            return null;
        }
        
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            System.out.println("Archivo JSON no existe: " + jsonPath);
            return null;
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            
            // Convertir a Map manteniendo toda la estructura
            @SuppressWarnings("unchecked")
            Map<String, Object> fullData = objectMapper.convertValue(rootNode, Map.class);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> predictions = (List<Map<String, Object>>) fullData.get("predictions");
            if (predictions == null) {
                return fullData;
            }
            
            // Si se proporciona un usuario, filtrar predictions
            if (user != null && !user.isEmpty()) {
                List<Map<String, Object>> filteredPredictions = new ArrayList<>();
                
                for (Map<String, Object> prediction : predictions) {
                    // Verificar si el usuario es el attacker o victim
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
                
                // Crear nuevo objeto con predictions filtradas
                Map<String, Object> filteredData = new HashMap<>(fullData);
                filteredData.put("predictions", filteredPredictions);
                filteredData.put("total_kills", filteredPredictions.size());
                
                System.out.println("Filtrados " + filteredPredictions.size() + " predictions para usuario " + user);
                return filteredData;
            }
            
            return fullData;
        } catch (IOException e) {
            System.err.println("Error leyendo archivo JSON " + jsonPath + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Helper para obtener valores anidados de un Map
     */
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

