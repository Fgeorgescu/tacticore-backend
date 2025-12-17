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

import org.springframework.core.io.ByteArrayResource;

@Service
public class MLServiceClient {
    
    @Value("${ml.service.url:http://3.91.78.196:8000}")
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
            return simulateMLResponse(file.getOriginalFilename());
        } else {
            return callRealMLService(file);
        }
    }
    
    /**
     * Envía un archivo .dem (como bytes) al servicio ML para análisis.
     * Esta variante es útil cuando el archivo proviene de S3.
     * 
     * @param fileContent Contenido del archivo como byte array
     * @param fileName Nombre original del archivo
     * @return Respuesta del servicio ML con análisis de kills
     */
    public Map<String, Object> analyzeDemoFile(byte[] fileContent, String fileName) {
        if (simulationEnabled) {
            return simulateMLResponse(fileName);
        } else {
            return callRealMLServiceWithBytes(fileContent, fileName);
        }
    }
    
    private Map<String, Object> simulateMLResponse(String fileName) {
        try {
            if (fileName == null || !fileName.endsWith(".dem")) {
                throw new RuntimeException("Invalid DEM file: " + fileName);
            }
            
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String jsonFileName;
            String baseNameLower = baseName.toLowerCase();
            
            if ("nuke".equals(baseNameLower)) {
                jsonFileName = "de_nuke.json";
            } else if (baseNameLower.startsWith("de_")) {
                jsonFileName = baseName + ".json";
            } else {
                jsonFileName = baseName + ".json";
                Path directPath = Paths.get(jsonDirectory, jsonFileName);
                
                if (!Files.exists(directPath)) {
                    jsonFileName = "de_" + baseName + ".json";
                }
            }
            
            Path jsonPath = Paths.get(jsonDirectory, jsonFileName);
            
            if (!Files.exists(jsonPath)) {
                throw new RuntimeException("JSON file not found: " + jsonFileName + 
                    " (searched for: " + fileName + ")");
            }
            
            String jsonContent = Files.readString(jsonPath);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = objectMapper.readValue(jsonContent, Map.class);
            
            return response;
            
        } catch (IOException e) {
            throw new RuntimeException("Error reading simulation JSON file: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Object> callRealMLService(MultipartFile file) {
        String analyzeUrl = mlServiceUrl + "/analyze-demo";
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(fileParamName, file.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(analyzeUrl, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                return responseBody;
            } else {
                String errorMsg = "Error in ML service response: " + response.getStatusCode();
                System.err.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorMsg = "HTTP error communicating with ML service (" + analyzeUrl + "): " + 
                            e.getStatusCode() + " - " + e.getResponseBodyAsString();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            String errorMsg = "Could not connect to ML service (" + analyzeUrl + "). " +
                            "Verify that the service is running and accessible. Error: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error communicating with ML service: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    private Map<String, Object> callRealMLServiceWithBytes(byte[] fileContent, String fileName) {
        String analyzeUrl = mlServiceUrl + "/analyze-demo";
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Crear un Resource desde los bytes
            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add(fileParamName, fileResource);
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(analyzeUrl, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                return responseBody;
            } else {
                String errorMsg = "Error in ML service response: " + response.getStatusCode();
                System.err.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String errorMsg = "HTTP error communicating with ML service (" + analyzeUrl + "): " + 
                            e.getStatusCode() + " - " + e.getResponseBodyAsString();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            String errorMsg = "Could not connect to ML service (" + analyzeUrl + "). " +
                            "Verify that the service is running and accessible. Error: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error communicating with ML service: " + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
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
