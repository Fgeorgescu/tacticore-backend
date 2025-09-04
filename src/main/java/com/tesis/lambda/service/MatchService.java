package com.tacticore.lambda.service;

import com.tacticore.lambda.model.MatchMetadata;
import com.tacticore.lambda.model.MatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MatchService {
    
    private static final String UPLOAD_DIR = "/tmp/matches/";
    private static final String DEM_EXTENSION = ".dem";
    private static final String VIDEO_EXTENSIONS = ".mp4,.avi,.mov,.mkv";
    
    public MatchService() {
        // Crear directorio de uploads si no existe
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating upload directory: " + e.getMessage());
        }
    }
    
    public MatchResponse uploadMatch(MultipartFile demFile, MultipartFile videoFile, MatchMetadata metadata) {
        String matchId = generateMatchId();
        
        try {
            // Validar archivo DEM
            validateDemFile(demFile);
            
            // Validar archivo de video (si se proporciona)
            if (videoFile != null && !videoFile.isEmpty()) {
                validateVideoFile(videoFile);
            }
            
            // Validar metadata
            validateMetadata(metadata);
            
            // Guardar archivo DEM
            String demFileName = saveFile(demFile, matchId, "dem");
            
            // Guardar archivo de video (si se proporciona)
            String videoFileName = null;
            if (videoFile != null && !videoFile.isEmpty()) {
                videoFileName = saveFile(videoFile, matchId, "video");
            }
            
            // Guardar metadata
            saveMetadata(matchId, metadata, demFileName, videoFileName);
            
            // Simular procesamiento asíncrono
            processMatchAsync(matchId);
            
            return MatchResponse.processing(matchId);
            
        } catch (Exception e) {
            System.err.println("Error uploading match: " + e.getMessage());
            return MatchResponse.failed(matchId, e.getMessage());
        }
    }
    
    private void validateDemFile(MultipartFile demFile) throws IllegalArgumentException {
        if (demFile == null || demFile.isEmpty()) {
            throw new IllegalArgumentException("DEM file is required");
        }
        
        String originalFilename = demFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(DEM_EXTENSION)) {
            throw new IllegalArgumentException("DEM file must have .dem extension");
        }
        
        if (demFile.getSize() > 500 * 1024 * 1024) { // 500MB limit
            throw new IllegalArgumentException("DEM file size exceeds 500MB limit");
        }
    }
    
    private void validateVideoFile(MultipartFile videoFile) throws IllegalArgumentException {
        String originalFilename = videoFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Video file must have a valid filename");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!VIDEO_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Video file must be one of: " + VIDEO_EXTENSIONS);
        }
        
        if (videoFile.getSize() > 2 * 1024 * 1024 * 1024L) { // 2GB limit
            throw new IllegalArgumentException("Video file size exceeds 2GB limit");
        }
    }
    
    private void validateMetadata(MatchMetadata metadata) throws IllegalArgumentException {
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata is required");
        }
        
        if (metadata.getPlayerName() == null || metadata.getPlayerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Player name is required");
        }
    }
    
    private String saveFile(MultipartFile file, String matchId, String fileType) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = String.format("%s_%s_%s%s", matchId, fileType, timestamp, extension);
        
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(file.getInputStream(), filePath);
        
        System.out.println("Saved " + fileType + " file: " + fileName);
        return fileName;
    }
    
    private void saveMetadata(String matchId, MatchMetadata metadata, String demFileName, String videoFileName) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        StringBuilder metadataContent = new StringBuilder();
        metadataContent.append("Match ID: ").append(matchId).append("\n");
        metadataContent.append("Upload Time: ").append(timestamp).append("\n");
        metadataContent.append("Player Name: ").append(metadata.getPlayerName()).append("\n");
        if (metadata.getNotes() != null && !metadata.getNotes().trim().isEmpty()) {
            metadataContent.append("Notes: ").append(metadata.getNotes()).append("\n");
        }
        metadataContent.append("DEM File: ").append(demFileName).append("\n");
        if (videoFileName != null) {
            metadataContent.append("Video File: ").append(videoFileName).append("\n");
        }
        
        String metadataFileName = matchId + "_metadata.txt";
        Path metadataPath = Paths.get(UPLOAD_DIR, metadataFileName);
        Files.write(metadataPath, metadataContent.toString().getBytes());
        
        System.out.println("Saved metadata: " + metadataFileName);
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
    
    private String generateMatchId() {
        return "match_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    private void processMatchAsync(String matchId) {
        // Simular procesamiento asíncrono
        System.out.println("Starting async processing for match: " + matchId);
        
        // En un entorno real, aquí se enviaría a una cola de procesamiento
        // Por ahora, solo simulamos el inicio del procesamiento
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simular tiempo de procesamiento
                System.out.println("Async processing completed for match: " + matchId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Processing interrupted for match: " + matchId);
            }
        }).start();
    }
}
