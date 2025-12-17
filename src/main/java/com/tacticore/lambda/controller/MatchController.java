package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.MatchMetadata;
import com.tacticore.lambda.model.MatchResponse;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.model.S3MatchUploadRequest;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.MLServiceClient;
import com.tacticore.lambda.service.S3Service;
import com.tacticore.lambda.service.SimulationDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MatchController {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DatabaseMatchService databaseMatchService;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private MLServiceClient mlServiceClient;
    
    @Autowired
    private SimulationDataMapper simulationDataMapper;
    
    @Autowired
    private S3Service s3Service;
    
    @PostMapping(value = "/matches", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> uploadMatch(
            @RequestParam("demFile") MultipartFile demFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "metadata", required = false) String metadataJson) {
        
        try {
            if (demFile == null || demFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "DEM file is required"));
            }
            
            MatchMetadata metadata = null;
            if (metadataJson != null && !metadataJson.trim().isEmpty()) {
                try {
                    metadata = objectMapper.readValue(metadataJson, MatchMetadata.class);
                } catch (Exception e) {
                    System.err.println("Error parsing metadata JSON: " + e.getMessage());
                    return ResponseEntity.badRequest()
                            .body(MatchResponse.failed("unknown", "Invalid metadata JSON format"));
                }
            }
            
            String matchId = "match_" + System.currentTimeMillis();
            
            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setMatchId(matchId);
            matchEntity.setFileName(demFile.getOriginalFilename());
            matchEntity.setMapName(metadata != null ? metadata.getNotes() : "Unknown");
            matchEntity.setStatus("processing");
            matchEntity.setHasVideo(videoFile != null && !videoFile.isEmpty());
            matchEntity.setTotalKills(null);
            matchEntity.setTickrate(null);
            
            saveMatch(matchEntity);
            
            try {
                Map<String, Object> mlResponse = mlServiceClient.analyzeDemoFile(demFile);
                
                SimulationDataMapper.SimulationResult result = simulationDataMapper.mapMLResponseToEntities(
                    mlResponse, matchId, demFile.getOriginalFilename()
                );
                
                databaseMatchService.updateMatchWithKills(
                    matchId, 
                    result.getTotalKills(), 
                    result.getTickrate(), 
                    result.getMapName(), 
                    result.getKillEntities(),
                    mlResponse
                );
                
                return ResponseEntity.ok(MatchResponse.completed(matchId));
                
            } catch (Exception e) {
                System.err.println("Error processing match: " + matchId + " - " + e.getMessage());
                updateMatchWithError(matchId, "Processing failed: " + e.getMessage());
                return ResponseEntity.internalServerError()
                        .body(MatchResponse.failed(matchId, "Match processing failed: " + e.getMessage()));
            }
            
        } catch (Exception e) {
            System.err.println("Error in match upload: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MatchResponse.failed("unknown", "Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para subir un match desde un archivo almacenado en S3.
     * En lugar de recibir el archivo directamente, recibe el bucket y key de S3.
     * 
     * @param request Contiene bucket, key y metadata opcional
     * @return MatchResponse con el resultado del procesamiento
     */
    @PostMapping(value = "/matches/s3", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MatchResponse> uploadMatchFromS3(@RequestBody S3MatchUploadRequest request) {
        
        try {
            // Validar parámetros requeridos
            if (request.getBucket() == null || request.getBucket().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "S3 bucket is required"));
            }
            
            if (request.getKey() == null || request.getKey().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "S3 key is required"));
            }
            
            String fileName = request.getFileName();
            System.out.println("📥 Processing match from S3: s3://" + request.getBucket() + "/" + request.getKey());
            
            // Verificar que el archivo existe en S3
            if (!s3Service.fileExists(request.getBucket(), request.getKey())) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "File not found in S3: s3://" + request.getBucket() + "/" + request.getKey()));
            }
            
            // Descargar archivo desde S3
            byte[] fileContent = s3Service.downloadFile(request.getBucket(), request.getKey());
            
            if (fileContent == null || fileContent.length == 0) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "Downloaded file is empty"));
            }
            
            MatchMetadata metadata = request.getMetadata();
            String matchId = "match_" + System.currentTimeMillis();
            
            // Crear entidad de match
            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setMatchId(matchId);
            matchEntity.setFileName(fileName);
            matchEntity.setMapName(metadata != null ? metadata.getNotes() : "Unknown");
            matchEntity.setStatus("processing");
            matchEntity.setHasVideo(false);
            matchEntity.setTotalKills(null);
            matchEntity.setTickrate(null);
            
            saveMatch(matchEntity);
            
            try {
                // Procesar con el servicio ML usando los bytes descargados
                Map<String, Object> mlResponse = mlServiceClient.analyzeDemoFile(fileContent, fileName);
                
                SimulationDataMapper.SimulationResult result = simulationDataMapper.mapMLResponseToEntities(
                    mlResponse, matchId, fileName
                );
                
                databaseMatchService.updateMatchWithKills(
                    matchId, 
                    result.getTotalKills(), 
                    result.getTickrate(), 
                    result.getMapName(), 
                    result.getKillEntities(),
                    mlResponse
                );
                
                System.out.println("✅ Match processed successfully: " + matchId);
                return ResponseEntity.ok(MatchResponse.completed(matchId));
                
            } catch (Exception e) {
                System.err.println("❌ Error processing match: " + matchId + " - " + e.getMessage());
                updateMatchWithError(matchId, "Processing failed: " + e.getMessage());
                return ResponseEntity.internalServerError()
                        .body(MatchResponse.failed(matchId, "Match processing failed: " + e.getMessage()));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in S3 match upload: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MatchResponse.failed("unknown", "Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/matches/{matchId}/status")
    public ResponseEntity<MatchResponse> getMatchStatus(@PathVariable String matchId) {
        try {
            String status = databaseMatchService.getMatchStatus(matchId);
            
            if ("not_found".equals(status)) {
                return ResponseEntity.notFound().build();
            }
            
            String message = switch (status) {
                case "completed" -> "Match processing completed successfully";
                case "failed" -> "Match processing failed";
                case "processing" -> "Match is being processed";
                default -> "Unknown status";
            };
            
            return ResponseEntity.ok(new MatchResponse(matchId, status, message));
            
        } catch (Exception e) {
            System.err.println("Error getting match status: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MatchResponse.failed(matchId, "Error retrieving match status"));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tacti-Core Backend is running!");
    }
    
    private void saveMatch(MatchEntity matchEntity) {
        databaseMatchService.saveMatch(matchEntity);
        chatService.addChatMessage(
            matchEntity.getMatchId(), 
            "Bot", 
            "Si tienes una duda, podes realizarme cualquier consulta"
        );
    }
    
    private void updateMatchWithError(String matchId, String errorMessage) {
        databaseMatchService.updateMatchWithError(matchId, errorMessage);
    }
}
