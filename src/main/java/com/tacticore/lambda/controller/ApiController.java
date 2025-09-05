package com.tacticore.lambda.controller;

import com.tacticore.lambda.model.*;
import com.tacticore.lambda.model.dto.ChatMessageDto;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.KillAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    
    @Autowired
    private DatabaseMatchService databaseMatchService;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private KillAnalysisService killAnalysisService;
    
    // Mock data for kills (keeping this for now as it's not yet migrated to DB)
    private final Map<String, List<Kill>> mockKills = new HashMap<>();
    
    public ApiController() {
        // Initialize mock kills data (temporary until kills are fully migrated)
        mockKills.put("1", Arrays.asList(
            new Kill(1, "Player1", "Enemy1", "AK-47", true, 3, "1:45", new Kill.TeamAlive(4, 3), "Long A"),
            new Kill(2, "Player1", "Enemy2", "AK-47", true, 3, "1:42", new Kill.TeamAlive(4, 2), "Long A"),
            new Kill(3, "Enemy3", "Player1", "AWP", false, 5, "0:30", new Kill.TeamAlive(2, 3), "Mid"),
            new Kill(4, "Player1", "Enemy4", "M4A4", true, 8, "1:15", new Kill.TeamAlive(3, 4), "Site B"),
            new Kill(5, "Player1", "Enemy5", "Glock-18", false, 12, "0:45", new Kill.TeamAlive(1, 2), "Tunnels")
        ));
    }
    
    // GET /api/matches
    @GetMapping("/matches")
    public ResponseEntity<Map<String, Object>> getMatches(@RequestParam(required = false) String user) {
        List<MatchDto> matches = databaseMatchService.getAllMatches();
        
        // Si se especifica un usuario, filtrar matches que contengan kills de ese usuario
        if (user != null && !user.isEmpty()) {
            matches = databaseMatchService.getMatchesByUser(user);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("matches", matches);
        if (user != null) {
            response.put("filteredBy", user);
        }
        return ResponseEntity.ok(response);
    }
    
    // GET /api/matches/{id}
    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchDto> getMatch(@PathVariable String id) {
        Optional<MatchDto> match = databaseMatchService.getMatchById(id);
        
        if (match.isPresent()) {
            return ResponseEntity.ok(match.get());
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // DELETE /api/matches/{id}
    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Map<String, String>> deleteMatch(@PathVariable String id) {
        if (databaseMatchService.existsMatch(id)) {
            databaseMatchService.deleteMatch(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Match deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // GET /api/matches/{id}/kills
    @GetMapping("/matches/{id}/kills")
    public ResponseEntity<Object> getMatchKills(@PathVariable String id, @RequestParam(required = false) String user) {
        // Consultar kills desde la base de datos
        List<KillEntity> killEntities;
        
        if (user != null && !user.isEmpty()) {
            // Filtrar kills por usuario específico
            killEntities = killAnalysisService.getKillsByUser(user);
        } else {
            // Obtener todos los kills (necesitamos implementar este método)
            killEntities = killAnalysisService.getAllKills();
        }
        
        // Convertir entidades a DTOs para la respuesta
        List<Map<String, Object>> kills = killEntities.stream()
                .map(kill -> {
                    Map<String, Object> killDto = new HashMap<>();
                    killDto.put("killId", kill.getKillId());
                    killDto.put("attacker", kill.getAttacker());
                    killDto.put("victim", kill.getVictim());
                    killDto.put("weapon", kill.getWeapon());
                    killDto.put("headshot", kill.getHeadshot());
                    killDto.put("round", kill.getRound());
                    killDto.put("distance", kill.getDistance());
                    killDto.put("timeInRound", kill.getTimeInRound());
                    killDto.put("side", kill.getSide());
                    killDto.put("place", kill.getPlace());
                    return killDto;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("kills", kills);
        response.put("matchId", id);
        if (user != null) {
            response.put("filteredBy", user);
        }
        return ResponseEntity.ok(response);
    }
    
    // GET /api/matches/{id}/chat
    @GetMapping("/matches/{id}/chat")
    public ResponseEntity<List<ChatMessageDto>> getMatchChat(@PathVariable String id) {
        List<ChatMessageDto> messages = chatService.getChatMessages(id);
        return ResponseEntity.ok(messages);
    }
    
    // POST /api/matches/{id}/chat
    @PostMapping("/matches/{id}/chat")
    public ResponseEntity<ChatMessageDto> sendChatMessage(@PathVariable String id, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String user = request.get("user");
        
        if (message == null || user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if match exists
        if (!databaseMatchService.existsMatch(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Create new message using service
        ChatMessageDto newMessage = chatService.addChatMessage(id, user, message);
        
        return ResponseEntity.status(201).body(newMessage);
    }
    
    // POST /api/upload/dem
    @PostMapping("/upload/dem")
    public ResponseEntity<Map<String, Object>> uploadDem(@RequestParam("file") MultipartFile file,
                                                       @RequestParam(value = "metadata", required = false) String metadata) {
        try {
            // Validar archivo
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se proporcionó ningún archivo"));
            }
            
            // Validar que sea un archivo .dem
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".dem")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo debe ser un archivo .dem válido"));
            }
            
            // Generar ID único para el archivo
            String fileId = "dem_" + System.currentTimeMillis();
            
            // Simular procesamiento con el modelo de IA
            AIModelResponse aiResponse = createMockAIResponse(originalFilename);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", fileId);
            response.put("fileName", originalFilename);
            response.put("status", "processed");
            response.put("aiResponse", aiResponse);
            response.put("totalKills", aiResponse.getTotalKills());
            response.put("map", aiResponse.getMap());
            response.put("tickrate", aiResponse.getTickrate());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error procesando archivo: " + e.getMessage()));
        }
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
    
    // Método temporal para simular respuesta de IA
    private AIModelResponse createMockAIResponse(String fileName) {
        AIModelResponse response = new AIModelResponse();
        response.setTotalKills(143);
        response.setMap("Unknown");
        response.setTickrate(64);
        response.setStatus("success");
        return response;
    }
}
