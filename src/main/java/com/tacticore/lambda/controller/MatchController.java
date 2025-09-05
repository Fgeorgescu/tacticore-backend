package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.MatchMetadata;
import com.tacticore.lambda.model.MatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MatchController {
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
            
            // Process the match upload
            MatchResponse response = createMockMatchResponse(demFile, videoFile, metadata);
            
            if ("failed".equals(response.getStatus())) {
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error processing match upload: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MatchResponse.failed("unknown", "Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/matches/{matchId}/status")
    public ResponseEntity<MatchResponse> getMatchStatus(@PathVariable String matchId) {
        try {
            // Por ahora, simulamos obtener el estado de la partida
            // En un entorno real, esto consultaría una base de datos
            System.out.println("Getting status for match: " + matchId);
            
            // Simular diferentes estados basados en el ID
            String status = matchId.contains("completed") ? "completed" : 
                           matchId.contains("failed") ? "failed" : "processing";
            
            String message = switch (status) {
                case "completed" -> "Match processing completed successfully";
                case "failed" -> "Match processing failed";
                default -> "Match is being processed";
            };
            
            MatchResponse response = new MatchResponse(matchId, status, message);
            return ResponseEntity.ok(response);
            
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
    
    // Método temporal para simular respuesta de match upload
    private MatchResponse createMockMatchResponse(MultipartFile demFile, MultipartFile videoFile, MatchMetadata metadata) {
        String matchId = "match_" + System.currentTimeMillis();
        return MatchResponse.processing(matchId);
    }
}
