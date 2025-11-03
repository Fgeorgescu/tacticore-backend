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
import com.tacticore.lambda.service.JsonMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.util.Optional;

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
    
    @Autowired
    private JsonMatchService jsonMatchService;
    
    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    
    @GetMapping("/matches")
    public ResponseEntity<Map<String, Object>> getMatches(@RequestParam(required = false) String user) {
        List<MatchDto> matches = databaseMatchService.getAllMatches();
        
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
    
    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchDto> getMatch(@PathVariable String id) {
        Optional<MatchDto> match = databaseMatchService.getMatchById(id);
        
        if (match.isPresent()) {
            return ResponseEntity.ok(match.get());
        }
        
        return ResponseEntity.notFound().build();
    }
    
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
    
    @GetMapping("/matches/{id}/kills")
    public ResponseEntity<Object> getMatchKills(@PathVariable String id, @RequestParam(required = false) String user) {
        Optional<MatchDto> matchDto = databaseMatchService.getMatchById(id);
        if (matchDto.isPresent()) {
            String mlResponseJson = databaseMatchService.getMlResponseJson(id);
            if (mlResponseJson != null && !mlResponseJson.isEmpty()) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> jsonData = objectMapper.readValue(mlResponseJson, Map.class);
                    
                    if (user != null && !user.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> predictions = (List<Map<String, Object>>) jsonData.get("predictions");
                        if (predictions != null) {
                            List<Map<String, Object>> filteredPredictions = new ArrayList<>();
                            for (Map<String, Object> prediction : predictions) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> context = (Map<String, Object>) prediction.get("context");
                                if (context != null) {
                                    String attackerName = String.valueOf(context.get("attacker_name"));
                                    String victimName = String.valueOf(context.get("victim_name"));
                                    
                                    if (user.equalsIgnoreCase(attackerName) || user.equalsIgnoreCase(victimName)) {
                                        filteredPredictions.add(prediction);
                                    }
                                }
                            }
                            Map<String, Object> filteredData = new HashMap<>(jsonData);
                            filteredData.put("predictions", filteredPredictions);
                            filteredData.put("total_kills", filteredPredictions.size());
                            jsonData = filteredData;
                        }
                    }
                    
                    return ResponseEntity.ok(jsonData);
                } catch (Exception e) {
                    System.err.println("Error parsing JSON from database: " + e.getMessage());
                }
            }
        }
        
        Map<String, Object> jsonData = jsonMatchService.getMatchKillsFromJson(id, user);
        
        if (jsonData == null && matchDto.isPresent() && matchDto.get().getFileName() != null) {
            String fileName = matchDto.get().getFileName();
            jsonData = jsonMatchService.getMatchKillsFromJsonByFileName(id, fileName, user);
        }
        
        if (jsonData != null) {
            return ResponseEntity.ok(jsonData);
        }
        List<KillEntity> killEntities;
        
        if (user != null && !user.isEmpty()) {
            killEntities = killAnalysisService.getKillsByUser(user).stream()
                    .filter(kill -> id.equals(kill.getMatchId()))
                    .collect(Collectors.toList());
        } else {
            killEntities = killAnalysisService.getAllKills().stream()
                    .filter(kill -> id.equals(kill.getMatchId()))
                    .collect(Collectors.toList());
        }
        
        List<KillEntity> sortedKills = killEntities.stream()
                .sorted(Comparator.comparing(KillEntity::getRound)
                        .thenComparing(KillEntity::getTimeInRound))
                .collect(Collectors.toList());

        List<Map<String, Object>> kills = new ArrayList<>();
        Map<Integer, Map<String, Integer>> roundTeamCounts = new HashMap<>();
        
        for (KillEntity kill : sortedKills) {
            int round = kill.getRound();
            String victimSide = kill.getSide();
            
            roundTeamCounts.putIfAbsent(round, new HashMap<>());
            Map<String, Integer> teamCounts = roundTeamCounts.get(round);
            teamCounts.putIfAbsent("ct", 5);
            teamCounts.putIfAbsent("t", 5);
            
            if ("ct".equals(victimSide)) {
                teamCounts.put("ct", Math.max(0, teamCounts.get("ct") - 1));
            } else if ("t".equals(victimSide)) {
                teamCounts.put("t", Math.max(0, teamCounts.get("t") - 1));
            }
            
            Map<String, Object> killDto = new HashMap<>();
            killDto.put("id", kill.getKillId().hashCode());
            killDto.put("killer", kill.getAttacker());
            killDto.put("victim", kill.getVictim());
            killDto.put("weapon", kill.getWeapon());
            killDto.put("isGoodPlay", kill.getHeadshot() || kill.getDistance() > 500);
            killDto.put("round", kill.getRound());
            killDto.put("time", String.format("%.1fs", kill.getTimeInRound()));
            killDto.put("teamAlive", Map.of("ct", teamCounts.get("ct"), "t", teamCounts.get("t")));
            killDto.put("position", kill.getPlace());
            kills.add(killDto);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("kills", kills);
        response.put("matchId", id);
        if (user != null) {
            response.put("filteredBy", user);
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/matches/{id}/chat")
    public ResponseEntity<List<ChatMessageDto>> getMatchChat(@PathVariable String id) {
        List<ChatMessageDto> messages = chatService.getChatMessages(id);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/matches/{id}/chat")
    public ResponseEntity<ChatMessageDto> sendChatMessage(@PathVariable String id, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String user = request.get("user");
        
        if (message == null || user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!databaseMatchService.existsMatch(id)) {
            return ResponseEntity.notFound().build();
        }
        
        ChatMessageDto newMessage = chatService.addChatMessage(id, user, message);
        
        return ResponseEntity.status(201).body(newMessage);
    }
    
    @PostMapping("/upload/dem")
    public ResponseEntity<Map<String, Object>> uploadDem(@RequestParam("file") MultipartFile file,
                                                       @RequestParam(value = "metadata", required = false) String metadata) {
        try {
            if (file == null || file.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "No se proporcionó ningún archivo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".dem")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "El archivo debe ser un archivo .dem válido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            String fileId = "dem_" + System.currentTimeMillis();
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
    
    @PostMapping("/upload/process")
    public ResponseEntity<Map<String, Object>> processMatch(@RequestBody Map<String, String> request) {
        String matchId = request.get("matchId");
        
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", matchId);
        response.put("status", "processing");
        response.put("estimatedTime", 300);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/analytics/historical")
    public ResponseEntity<Map<String, Object>> getHistoricalAnalytics(
            @RequestParam(value = "timeRange", defaultValue = "all") String timeRange,
            @RequestParam(value = "metric", defaultValue = "kdr") String metric) {
        
        List<AnalyticsData> data = analyticsService.getHistoricalAnalytics(timeRange, metric);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats(@RequestParam(required = false) String user) {
        DashboardStats stats = analyticsService.getDashboardStats(user);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/maps")
    public ResponseEntity<List<String>> getMaps() {
        List<String> maps = gameDataService.getActiveMapNames();
        return ResponseEntity.ok(maps);
    }
    
    @GetMapping("/weapons")
    public ResponseEntity<List<String>> getWeapons() {
        List<String> weapons = gameDataService.getActiveWeaponNames();
        return ResponseEntity.ok(weapons);
    }
    
    private AIModelResponse createMockAIResponse(String fileName) {
        AIModelResponse response = new AIModelResponse();
        response.setTotalKills(143);
        response.setMap("Unknown");
        response.setTickrate(64);
        response.setStatus("success");
        return response;
    }
}
