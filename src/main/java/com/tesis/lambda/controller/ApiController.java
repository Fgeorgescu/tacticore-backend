package com.tesis.lambda.controller;

import com.tesis.lambda.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    
    // Mock data for matches
    private final List<Match> mockMatches = Arrays.asList(
        new Match("1", "dust2_ranked_2024.dem", true, "Dust2", "Ranked", 24, 18, 8, 3, "32:45", 8.5, LocalDate.of(2024, 1, 15)),
        new Match("2", "mirage_casual_2024.dem", false, "Mirage", "Casual", 16, 22, 4, 7, "28:12", 6.2, LocalDate.of(2024, 1, 14)),
        new Match("3", "inferno_training_2024.dem", true, "Inferno", "Entrenamiento", 31, 12, 12, 2, "25:30", 9.1, LocalDate.of(2024, 1, 13))
    );
    
    // Mock data for kills
    private final Map<String, List<Kill>> mockKills = new HashMap<>();
    
    // Mock data for chat messages
    private final Map<String, List<ChatMessage>> mockChatMessages = new HashMap<>();
    
    public ApiController() {
        // Initialize mock kills data
        mockKills.put("1", Arrays.asList(
            new Kill(1, "Player1", "Enemy1", "AK-47", true, 3, "1:45", new Kill.TeamAlive(4, 3), "Long A"),
            new Kill(2, "Player1", "Enemy2", "AK-47", true, 3, "1:42", new Kill.TeamAlive(4, 2), "Long A"),
            new Kill(3, "Enemy3", "Player1", "AWP", false, 5, "0:30", new Kill.TeamAlive(2, 3), "Mid"),
            new Kill(4, "Player1", "Enemy4", "M4A4", true, 8, "1:15", new Kill.TeamAlive(3, 4), "Site B"),
            new Kill(5, "Player1", "Enemy5", "Glock-18", false, 12, "0:45", new Kill.TeamAlive(1, 2), "Tunnels")
        ));
        
        // Initialize mock chat messages
        mockChatMessages.put("1", Arrays.asList(
            new ChatMessage(1, "Analyst", "¿Qué opinas de la jugada en el round 3?", LocalDate.of(2024, 1, 15).atTime(14, 30)),
            new ChatMessage(2, "Player", "Fue una buena rotación, aproveché que estaban distraídos en B", LocalDate.of(2024, 1, 15).atTime(14, 32)),
            new ChatMessage(3, "Coach", "El timing fue perfecto, pero podrías haber usado mejor cobertura", LocalDate.of(2024, 1, 15).atTime(14, 35))
        ));
    }
    
    // GET /api/matches
    @GetMapping("/matches")
    public ResponseEntity<Map<String, Object>> getMatches() {
        Map<String, Object> response = new HashMap<>();
        response.put("matches", mockMatches);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/matches/{id}
    @GetMapping("/matches/{id}")
    public ResponseEntity<Match> getMatch(@PathVariable String id) {
        Optional<Match> match = mockMatches.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
        
        if (match.isPresent()) {
            Match matchData = match.get();
            // Add kills to the response
            if (mockKills.containsKey(id)) {
                // We'll return the match with kills in a separate endpoint
                return ResponseEntity.ok(matchData);
            }
            return ResponseEntity.ok(matchData);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // DELETE /api/matches/{id}
    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Map<String, String>> deleteMatch(@PathVariable String id) {
        // Mock deletion - in real implementation, this would delete from database
        Map<String, String> response = new HashMap<>();
        response.put("message", "Match deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/matches/{id}/kills
    @GetMapping("/matches/{id}/kills")
    public ResponseEntity<List<Kill>> getMatchKills(@PathVariable String id) {
        List<Kill> kills = mockKills.get(id);
        if (kills != null) {
            return ResponseEntity.ok(kills);
        }
        return ResponseEntity.notFound().build();
    }
    
    // GET /api/matches/{id}/chat
    @GetMapping("/matches/{id}/chat")
    public ResponseEntity<List<ChatMessage>> getMatchChat(@PathVariable String id) {
        List<ChatMessage> messages = mockChatMessages.get(id);
        if (messages != null) {
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.ok(new ArrayList<>()); // Return empty list if no messages
    }
    
    // POST /api/matches/{id}/chat
    @PostMapping("/matches/{id}/chat")
    public ResponseEntity<ChatMessage> sendChatMessage(@PathVariable String id, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String user = request.get("user");
        
        if (message == null || user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Get existing messages or create new list
        List<ChatMessage> messages = mockChatMessages.getOrDefault(id, new ArrayList<>());
        
        // Create new message
        ChatMessage newMessage = new ChatMessage(
            messages.size() + 1,
            user,
            message,
            java.time.LocalDateTime.now()
        );
        
        // Add to list
        messages.add(newMessage);
        mockChatMessages.put(id, messages);
        
        return ResponseEntity.status(201).body(newMessage);
    }
    
    // POST /api/upload/dem
    @PostMapping("/upload/dem")
    public ResponseEntity<Map<String, Object>> uploadDem(@RequestParam("file") MultipartFile file,
                                                       @RequestParam(value = "metadata", required = false) String metadata) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "match_" + System.currentTimeMillis());
        response.put("fileName", file.getOriginalFilename());
        response.put("status", "uploaded");
        
        return ResponseEntity.status(201).body(response);
    }
    
    // POST /api/upload/video
    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("matchId") String matchId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "video_" + System.currentTimeMillis());
        response.put("matchId", matchId);
        response.put("status", "uploaded");
        
        return ResponseEntity.status(201).body(response);
    }
    
    // POST /api/upload/process
    @PostMapping("/upload/process")
    public ResponseEntity<Map<String, Object>> processMatch(@RequestBody Map<String, String> request) {
        String matchId = request.get("matchId");
        String analysisType = request.getOrDefault("analysisType", "full");
        
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", matchId);
        response.put("status", "processing");
        response.put("estimatedTime", 300); // 5 minutes
        
        return ResponseEntity.ok(response);
    }
    
    // GET /api/analytics/historical
    @GetMapping("/analytics/historical")
    public ResponseEntity<Map<String, Object>> getHistoricalAnalytics(
            @RequestParam(value = "timeRange", defaultValue = "all") String timeRange,
            @RequestParam(value = "metric", defaultValue = "kdr") String metric) {
        
        List<AnalyticsData> data = Arrays.asList(
            new AnalyticsData(LocalDate.of(2024, 1, 1), 18, 22, 0.82, 6.5, 4, 8, 1),
            new AnalyticsData(LocalDate.of(2024, 1, 3), 21, 19, 1.11, 7.2, 6, 6, 2),
            new AnalyticsData(LocalDate.of(2024, 1, 5), 24, 18, 1.33, 8.1, 8, 4, 3),
            new AnalyticsData(LocalDate.of(2024, 1, 7), 19, 25, 0.76, 5.8, 3, 9, 4),
            new AnalyticsData(LocalDate.of(2024, 1, 9), 27, 16, 1.69, 8.7, 11, 3, 5),
            new AnalyticsData(LocalDate.of(2024, 1, 11), 22, 20, 1.1, 7.5, 7, 5, 6),
            new AnalyticsData(LocalDate.of(2024, 1, 13), 31, 12, 2.58, 9.1, 12, 2, 7),
            new AnalyticsData(LocalDate.of(2024, 1, 15), 24, 18, 1.33, 8.5, 8, 3, 8)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
    
    // GET /api/analytics/dashboard
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = new DashboardStats(8, 185, 150, 58, 40, 7.6, 1.23);
        return ResponseEntity.ok(stats);
    }
    
    // GET /api/maps
    @GetMapping("/maps")
    public ResponseEntity<List<String>> getMaps() {
        List<String> maps = Arrays.asList("Dust2", "Mirage", "Inferno", "Cache", "Overpass", "Nuke", "Train");
        return ResponseEntity.ok(maps);
    }
    
    // GET /api/weapons
    @GetMapping("/weapons")
    public ResponseEntity<List<String>> getWeapons() {
        List<String> weapons = Arrays.asList("AK-47", "M4A4", "AWP", "Glock-18", "USP-S", "Desert Eagle", "P250", "FAMAS", "Galil");
        return ResponseEntity.ok(weapons);
    }
}
