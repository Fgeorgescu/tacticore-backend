package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.MatchMetadata;
import com.tacticore.lambda.model.MatchResponse;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.service.DatabaseMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MatchController {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DatabaseMatchService databaseMatchService;
    
    @PostMapping(value = "/matches", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> uploadMatch(
            @RequestParam("demFile") MultipartFile demFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "metadata", required = false) String metadataJson) {
        
        try {
            System.out.println("Received match upload request");
            System.out.println("DEM file: " + (demFile != null ? demFile.getOriginalFilename() : "null"));
            System.out.println("Video file: " + (videoFile != null ? videoFile.getOriginalFilename() : "null"));
            System.out.println("Metadata: " + metadataJson);
            
            // Validar archivo DEM
            if (demFile == null || demFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MatchResponse.failed("unknown", "DEM file is required"));
            }
            
            // Parse metadata JSON
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
            
            // Generar ID único para la partida
            String matchId = "match_" + System.currentTimeMillis();
            
            // Crear partida en DB con estado "processing"
            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setMatchId(matchId);
            matchEntity.setFileName(demFile.getOriginalFilename());
            matchEntity.setMapName(metadata != null ? metadata.getNotes() : "Unknown");
            matchEntity.setStatus("processing");
            matchEntity.setHasVideo(videoFile != null && !videoFile.isEmpty());
            matchEntity.setTotalKills(null); // Se llenará después del procesamiento
            matchEntity.setTickrate(null); // Se llenará después del procesamiento
            
            // Guardar en DB (necesitamos agregar este método al servicio)
            saveMatch(matchEntity);
            
            // Simular procesamiento asíncrono
            simulateAsyncProcessing(matchId, demFile, videoFile, metadata);
            
            return ResponseEntity.ok(MatchResponse.processing(matchId));
            
        } catch (Exception e) {
            System.err.println("Error processing match upload: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MatchResponse.failed("unknown", "Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/matches/{matchId}/status")
    public ResponseEntity<MatchResponse> getMatchStatus(@PathVariable String matchId) {
        try {
            System.out.println("Getting status for match: " + matchId);
            
            // Obtener estado real desde la DB
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
    
    // Método para guardar partida en DB
    private void saveMatch(MatchEntity matchEntity) {
        databaseMatchService.saveMatch(matchEntity);
    }
    
    // Método para simular procesamiento asíncrono
    private void simulateAsyncProcessing(String matchId, MultipartFile demFile, MultipartFile videoFile, MatchMetadata metadata) {
        CompletableFuture.runAsync(() -> {
            try {
                // Simular delay de procesamiento (2-5 segundos)
                Thread.sleep(2000 + (int)(Math.random() * 3000));
                
                // Simular resultados del procesamiento
                int totalKills = 100 + (int)(Math.random() * 100); // 100-200 kills
                int tickrate = 64;
                String mapName = "Dust2"; // Por ahora fijo
                
                // Actualizar partida en DB con resultados
                updateMatchWithResults(matchId, totalKills, tickrate, mapName);
                
                System.out.println("Match processing completed for: " + matchId);
                
            } catch (InterruptedException e) {
                System.err.println("Processing interrupted for match: " + matchId);
                updateMatchWithError(matchId, "Processing interrupted");
            } catch (Exception e) {
                System.err.println("Error processing match: " + matchId + " - " + e.getMessage());
                updateMatchWithError(matchId, "Processing failed: " + e.getMessage());
            }
        });
    }
    
    // Método para actualizar partida con resultados
    private void updateMatchWithResults(String matchId, int totalKills, int tickrate, String mapName) {
        System.out.println("Updating match " + matchId + " with results: kills=" + totalKills + ", tickrate=" + tickrate + ", map=" + mapName);
        databaseMatchService.updateMatchWithResults(matchId, totalKills, tickrate, mapName);
    }
    
    // Método para actualizar partida con error
    private void updateMatchWithError(String matchId, String errorMessage) {
        System.out.println("Updating match " + matchId + " with error: " + errorMessage);
        databaseMatchService.updateMatchWithError(matchId, errorMessage);
    }
}
