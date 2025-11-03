package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.MatchMetadata;
import com.tacticore.lambda.model.MatchResponse;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.MLServiceClient;
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
