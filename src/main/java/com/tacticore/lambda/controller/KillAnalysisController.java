package com.tacticore.lambda.controller;

import com.tacticore.lambda.model.dto.KillAnalysisDto;
import com.tacticore.lambda.model.dto.PlayerStatsDto;
import com.tacticore.lambda.model.dto.RoundAnalysisDto;
import com.tacticore.lambda.service.KillAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class KillAnalysisController {
    
    @Autowired
    private KillAnalysisService killAnalysisService;
    
    // GET /api/analysis/overview
    @GetMapping("/overview")
    public ResponseEntity<KillAnalysisDto> getOverallAnalysis() {
        KillAnalysisDto analysis = killAnalysisService.getOverallAnalysis();
        return ResponseEntity.ok(analysis);
    }
    
    // GET /api/analysis/player/{playerName}
    @GetMapping("/player/{playerName}")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable String playerName) {
        PlayerStatsDto stats = killAnalysisService.getPlayerStats(playerName);
        return ResponseEntity.ok(stats);
    }
    
    // GET /api/analysis/round/{roundNumber}
    @GetMapping("/round/{roundNumber}")
    public ResponseEntity<RoundAnalysisDto> getRoundAnalysis(@PathVariable Integer roundNumber) {
        RoundAnalysisDto analysis = killAnalysisService.getRoundAnalysis(roundNumber);
        return ResponseEntity.ok(analysis);
    }
    
    // GET /api/analysis/rounds
    @GetMapping("/rounds")
    public ResponseEntity<Object> getAllRoundsAnalysis() {
        // Implementar análisis de todas las rondas si es necesario
        return ResponseEntity.ok(Map.of("message", "Análisis de todas las rondas - implementar según necesidad"));
    }
    
    // GET /api/analysis/players
    @GetMapping("/players")
    public ResponseEntity<Object> getAllPlayersAnalysis() {
        // Implementar análisis de todos los jugadores si es necesario
        return ResponseEntity.ok(Map.of("message", "Análisis de todos los jugadores - implementar según necesidad"));
    }
}
