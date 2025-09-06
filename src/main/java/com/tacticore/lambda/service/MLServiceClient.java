package com.tacticore.lambda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class MLServiceClient {
    
    @Value("${ml.service.url:http://ml-service:8000}")
    private String mlServiceUrl;
    
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
        try {
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Preparar body con el archivo
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("demo_file", file.getResource());
            
            // Crear request
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Hacer request al servicio ML
            String analyzeUrl = mlServiceUrl + "/analyze-demo";
            ResponseEntity<Map> response = restTemplate.postForEntity(analyzeUrl, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error en respuesta del servicio ML: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error comunicándose con el servicio ML: " + e.getMessage(), e);
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
            ResponseEntity<Map> response = restTemplate.getForEntity(modelInfoUrl, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error obteniendo información del modelo: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo información del modelo: " + e.getMessage(), e);
        }
    }
}
