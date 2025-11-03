package com.tacticore.lambda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class MLServiceClient {
    
    @Value("${ml.service.url:http://ml-service:8000}")
    private String mlServiceUrl;
    
    @Value("${ml.service.file-param-name:demo_file}")
    private String fileParamName;
    
    @Value("${simulation.enabled:false}")
    private boolean simulationEnabled;
    
    @Value("${simulation.json-directory:demos-jsons}")
    private String jsonDirectory;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final RestTemplate restTemplate;
    
    public MLServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Envía un archivo .dem al servicio ML para análisis
     * 
     * @param file Archivo .dem a analizar
     * @return Respuesta del servicio ML con análisis de kills
     */
    public Map<String, Object> analyzeDemoFile(MultipartFile file) {
        if (simulationEnabled) {
            return simulateMLResponse(file);
        } else {
            return callRealMLService(file);
        }
    }
    
    /**
     * Simula la respuesta del servicio ML usando archivos JSON locales
     */
    private Map<String, Object> simulateMLResponse(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".dem")) {
                throw new RuntimeException("Archivo DEM inválido: " + fileName);
            }
            
            // Extraer nombre base del archivo (sin extensión)
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            // Mapeo de nombres de archivos DEM a sus archivos JSON correspondientes
            // Si el nombre base no tiene prefijo "de_", se añade automáticamente para ciertos mapas
            String jsonFileName;
            String baseNameLower = baseName.toLowerCase();
            
            // Casos especiales: si el archivo se llama "nuke.dem", buscar "de_nuke.json"
            if ("nuke".equals(baseNameLower)) {
                jsonFileName = "de_nuke.json";
            } else if (baseNameLower.startsWith("de_")) {
                // Si ya tiene prefijo "de_", usarlo tal cual
                jsonFileName = baseName + ".json";
            } else {
                // Para otros casos, intentar primero el nombre directo, luego con "de_"
                jsonFileName = baseName + ".json";
                Path directPath = Paths.get(jsonDirectory, jsonFileName);
                
                if (!Files.exists(directPath)) {
                    // Intentar con prefijo "de_"
                    jsonFileName = "de_" + baseName + ".json";
                }
            }
            
            Path jsonPath = Paths.get(jsonDirectory, jsonFileName);
            
            if (!Files.exists(jsonPath)) {
                throw new RuntimeException("No se encontró archivo JSON correspondiente: " + jsonFileName + 
                    " (buscado para: " + fileName + ")");
            }
            
            // Leer y parsear el archivo JSON
            String jsonContent = Files.readString(jsonPath);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = objectMapper.readValue(jsonContent, Map.class);
            
            System.out.println("Simulación ML: Usando archivo " + jsonFileName + " para " + fileName);
            return response;
            
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo JSON de simulación: " + e.getMessage(), e);
        }
    }
    
    /**
     * Llama al servicio ML real usando MultipartFile
     */
    private Map<String, Object> callRealMLService(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String analyzeUrl = mlServiceUrl + "/analyze-demo";
        
        try {
            long fileSize = file.getSize();
            System.out.println("🚀 Llamando al servicio ML real: " + analyzeUrl);
            System.out.println("📁 Archivo: " + fileName + " (tamaño: " + fileSize + " bytes)");
            
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Preparar body con el archivo usando el nombre de parámetro configurado
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(fileParamName, file.getResource());
            
            System.out.println("📤 Enviando archivo con parámetro: " + fileParamName);
            
            // Crear request
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Hacer request al servicio ML
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(analyzeUrl, requestEntity, Map.class);
            
            System.out.println("✅ Respuesta del servicio ML - Status: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                System.out.println("✅ Análisis completado exitosamente para: " + fileName);
                return responseBody;
            } else {
                String errorMsg = "Error en respuesta del servicio ML: " + response.getStatusCode();
                System.err.println("❌ " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorMsg = "Error HTTP al comunicarse con el servicio ML (" + analyzeUrl + "): " + 
                            e.getStatusCode() + " - " + e.getResponseBodyAsString();
            System.err.println("❌ " + errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            String errorMsg = "No se pudo conectar al servicio ML (" + analyzeUrl + "). " +
                            "Verifica que el servicio esté corriendo y accesible. Error: " + e.getMessage();
            System.err.println("❌ " + errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error inesperado comunicándose con el servicio ML: " + e.getMessage();
            System.err.println("❌ " + errorMsg);
            e.printStackTrace();
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    /**
     * Verifica el estado del servicio ML
     * 
     * @return true si el servicio está disponible, false en caso contrario
     */
    public boolean isServiceAvailable() {
        try {
            String healthUrl = mlServiceUrl + "/";
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene información del modelo ML cargado
     * 
     * @return Información del modelo
     */
    public Map<String, Object> getModelInfo() {
        try {
            String modelInfoUrl = mlServiceUrl + "/model-info";
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(modelInfoUrl, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                return responseBody;
            } else {
                throw new RuntimeException("Error obteniendo información del modelo: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo información del modelo: " + e.getMessage(), e);
        }
    }
}
