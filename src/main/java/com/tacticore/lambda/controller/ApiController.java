package com.tacticore.lambda.controller;

import com.tacticore.lambda.model.*;
import com.tacticore.lambda.model.dto.ChatMessageDto;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.service.AnalyticsService;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.GameDataService;
import com.tacticore.lambda.service.KillAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private GameDataService gameDataService;
    
    public ApiController() {
        // Constructor vacío - ya no usamos datos mock
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
                    killDto.put("id", kill.getKillId().hashCode()); // Convertir string a int
                    killDto.put("killer", kill.getAttacker());
                    killDto.put("victim", kill.getVictim());
                    killDto.put("weapon", kill.getWeapon());
                    killDto.put("isGoodPlay", kill.getHeadshot() || kill.getDistance() > 500); // Lógica simple para determinar si es buena jugada
                    killDto.put("round", kill.getRound());
                    killDto.put("time", String.format("%.1fs", kill.getTimeInRound()));
                    killDto.put("teamAlive", Map.of("ct", 5, "t", 5)); // Mock data - en un sistema real esto vendría de la base de datos
                    killDto.put("position", kill.getPlace());
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
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "No se proporcionó ningún archivo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Validar que sea un archivo .dem
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".dem")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "El archivo debe ser un archivo .dem válido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Generar ID único para el archivo
            String fileId = "dem_" + System.currentTimeMillis();
            
            // Simular procesamiento con el modelo de IA
            AIModelResponse aiResponse = createMockAIResponse(originalFilename);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Archivo .dem procesado exitosamente");
            response.put("id", fileId);
            response.put("fileName", originalFilename);
            response.put("status", "processed");
            response.put("aiResponse", aiResponse);
            response.put("totalKills", aiResponse.getTotalKills());
            response.put("map", aiResponse.getMap());
            response.put("tickrate", aiResponse.getTickrate());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error procesando archivo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    // POST /api/upload/video
    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file,
                                                         @RequestParam(value = "matchId", required = false) String matchId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "video_" + System.currentTimeMillis());
        if (matchId != null) {
            response.put("matchId", matchId);
        }
        response.put("status", "uploaded");
        
        return ResponseEntity.status(201).body(response);
    }
    
    // POST /api/upload/process
    @PostMapping("/upload/process")
    public ResponseEntity<Map<String, Object>> processMatch(@RequestBody Map<String, String> request) {
        String matchId = request.get("matchId");
        
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
        
        List<AnalyticsData> data = analyticsService.getHistoricalAnalytics(timeRange, metric);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
    
    // GET /api/analytics/dashboard
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    // GET /api/maps
    @GetMapping("/maps")
    public ResponseEntity<List<String>> getMaps() {
        List<String> maps = gameDataService.getActiveMapNames();
        return ResponseEntity.ok(maps);
    }
    
    // GET /api/weapons
    @GetMapping("/weapons")
    public ResponseEntity<List<String>> getWeapons() {
        List<String> weapons = gameDataService.getActiveWeaponNames();
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
