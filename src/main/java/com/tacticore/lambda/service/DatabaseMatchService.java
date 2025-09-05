package com.tacticore.lambda.service;

import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatabaseMatchService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    public List<MatchDto> getAllMatches() {
        List<MatchEntity> entities = matchRepository.findAll();
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<MatchDto> getMatchById(String matchId) {
        return matchRepository.findByMatchId(matchId)
                .map(this::convertToDto);
    }
    
    public boolean existsMatch(String matchId) {
        return matchRepository.existsByMatchId(matchId);
    }
    
    public void deleteMatch(String matchId) {
        matchRepository.findByMatchId(matchId)
                .ifPresent(matchRepository::delete);
    }
    
    private MatchDto convertToDto(MatchEntity entity) {
        MatchDto dto = new MatchDto();
        dto.setId(entity.getMatchId());
        dto.setFileName(entity.getFileName());
        dto.setHasVideo(entity.getHasVideo());
        dto.setMap(entity.getMapName());
        dto.setGameType("Ranked"); // Default value
        dto.setKills(entity.getTotalKills());
        dto.setDeaths(calculateDeaths(entity.getTotalKills())); // Mock calculation
        dto.setGoodPlays(calculateGoodPlays(entity.getTotalKills()));
        dto.setBadPlays(calculateBadPlays(entity.getTotalKills()));
        dto.setDuration(calculateDuration(entity.getTotalKills()));
        dto.setScore(calculateScore(entity.getTotalKills()));
        dto.setDate(LocalDate.now().minusDays((int)(Math.random() * 30))); // Random date within last 30 days
        return dto;
    }
    
    private int calculateDeaths(int kills) {
        // Mock calculation: deaths are usually 70-90% of kills
        return (int)(kills * (0.7 + Math.random() * 0.2));
    }
    
    private int calculateGoodPlays(int kills) {
        // Mock calculation: good plays are 30-50% of kills
        return (int)(kills * (0.3 + Math.random() * 0.2));
    }
    
    private int calculateBadPlays(int kills) {
        // Mock calculation: bad plays are 10-20% of kills
        return (int)(kills * (0.1 + Math.random() * 0.1));
    }
    
    private String calculateDuration(int kills) {
        // Mock calculation: duration based on kills (roughly 1.5 minutes per kill)
        int totalSeconds = kills * 90 + (int)(Math.random() * 300); // Add random variance
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private double calculateScore(int kills) {
        // Mock calculation: score based on kills with some variance
        return 5.0 + (kills * 0.2) + (Math.random() * 2.0);
    }
}