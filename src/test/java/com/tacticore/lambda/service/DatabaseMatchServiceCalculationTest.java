package com.tacticore.lambda.service;

import com.tacticore.lambda.repository.KillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DatabaseMatchServiceCalculationTest {

    @Mock
    private KillRepository killRepository;

    @InjectMocks
    private DatabaseMatchService databaseMatchService;

    @BeforeEach
    void setUp() {
        // No additional setup needed for unit tests
    }

    @Test
    void testCalculateDurationConsistency() {
        // Given
        int kills = 25;

        // When - Call the method multiple times
        String duration1 = invokeCalculateDuration(kills);
        String duration2 = invokeCalculateDuration(kills);
        String duration3 = invokeCalculateDuration(kills);

        // Then - All results should be identical
        assertEquals(duration1, duration2, "Duration should be consistent for same kills");
        assertEquals(duration2, duration3, "Duration should be consistent for same kills");
        assertEquals(duration1, duration3, "Duration should be consistent for same kills");
    }

    @Test
    void testCalculateDurationDifferentKills() {
        // Given
        int kills1 = 10;
        int kills2 = 20;

        // When
        String duration1 = invokeCalculateDuration(kills1);
        String duration2 = invokeCalculateDuration(kills2);

        // Then - Different kills should produce different durations
        assertNotEquals(duration1, duration2, "Different kills should produce different durations");
        
        // Verify format (MM:SS)
        assertTrue(duration1.matches("\\d+:\\d{2}"), "Duration should be in MM:SS format");
        assertTrue(duration2.matches("\\d+:\\d{2}"), "Duration should be in MM:SS format");
    }

    @Test
    void testCalculateGoodPlaysConsistency() {
        // Given
        int kills = 30;

        // When - Call the method multiple times
        int goodPlays1 = invokeCalculateGoodPlays(kills);
        int goodPlays2 = invokeCalculateGoodPlays(kills);
        int goodPlays3 = invokeCalculateGoodPlays(kills);

        // Then - All results should be identical
        assertEquals(goodPlays1, goodPlays2, "Good plays should be consistent for same kills");
        assertEquals(goodPlays2, goodPlays3, "Good plays should be consistent for same kills");
        assertEquals(goodPlays1, goodPlays3, "Good plays should be consistent for same kills");
    }

    @Test
    void testCalculateGoodPlaysRange() {
        // Given
        int kills = 100;

        // When
        int goodPlays = invokeCalculateGoodPlays(kills);

        // Then - Should be within 30-50% of kills
        int minExpected = (int)(kills * 0.3);
        int maxExpected = (int)(kills * 0.5);
        
        assertTrue(goodPlays >= minExpected, 
            String.format("Good plays (%d) should be at least 30%% of kills (%d)", goodPlays, minExpected));
        assertTrue(goodPlays <= maxExpected, 
            String.format("Good plays (%d) should be at most 50%% of kills (%d)", goodPlays, maxExpected));
    }

    @Test
    void testCalculateBadPlaysConsistency() {
        // Given
        int kills = 40;

        // When - Call the method multiple times
        int badPlays1 = invokeCalculateBadPlays(kills);
        int badPlays2 = invokeCalculateBadPlays(kills);
        int badPlays3 = invokeCalculateBadPlays(kills);

        // Then - All results should be identical
        assertEquals(badPlays1, badPlays2, "Bad plays should be consistent for same kills");
        assertEquals(badPlays2, badPlays3, "Bad plays should be consistent for same kills");
        assertEquals(badPlays1, badPlays3, "Bad plays should be consistent for same kills");
    }

    @Test
    void testCalculateBadPlaysRange() {
        // Given
        int kills = 80;

        // When
        int badPlays = invokeCalculateBadPlays(kills);

        // Then - Should be within 10-20% of kills
        int minExpected = (int)(kills * 0.1);
        int maxExpected = (int)(kills * 0.2);
        
        assertTrue(badPlays >= minExpected, 
            String.format("Bad plays (%d) should be at least 10%% of kills (%d)", badPlays, minExpected));
        assertTrue(badPlays <= maxExpected, 
            String.format("Bad plays (%d) should be at most 20%% of kills (%d)", badPlays, maxExpected));
    }

    @Test
    void testCalculateUnifiedScoreConsistency() {
        // Given
        int kills = 15;
        int deaths = 10;
        int goodPlays = 8;
        int badPlays = 2;

        // When - Call the method multiple times
        double score1 = invokeCalculateUnifiedScore(kills, deaths, goodPlays, badPlays);
        double score2 = invokeCalculateUnifiedScore(kills, deaths, goodPlays, badPlays);
        double score3 = invokeCalculateUnifiedScore(kills, deaths, goodPlays, badPlays);

        // Then - All results should be identical
        assertEquals(score1, score2, 0.0001, "Unified score should be consistent for same inputs");
        assertEquals(score2, score3, 0.0001, "Unified score should be consistent for same inputs");
        assertEquals(score1, score3, 0.0001, "Unified score should be consistent for same inputs");
    }

    @Test
    void testCalculateUnifiedScoreRange() {
        // Given
        int kills = 50;
        int deaths = 30;
        int goodPlays = 20;
        int badPlays = 5;

        // When
        double score = invokeCalculateUnifiedScore(kills, deaths, goodPlays, badPlays);

        // Then - Should be within 1.0-10.0 range
        assertTrue(score >= 1.0, "Unified score should be at least 1.0");
        assertTrue(score <= 10.0, "Unified score should be at most 10.0");
    }

    @Test
    void testCalculateDeathsConsistency() {
        // Given
        int kills = 35;

        // When - Call the method multiple times
        int deaths1 = invokeCalculateDeaths(kills);
        int deaths2 = invokeCalculateDeaths(kills);
        int deaths3 = invokeCalculateDeaths(kills);

        // Then - All results should be identical
        assertEquals(deaths1, deaths2, "Deaths should be consistent for same kills");
        assertEquals(deaths2, deaths3, "Deaths should be consistent for same kills");
        assertEquals(deaths1, deaths3, "Deaths should be consistent for same kills");
    }

    @Test
    void testCalculateDeathsRange() {
        // Given
        int kills = 60;

        // When
        int deaths = invokeCalculateDeaths(kills);

        // Then - Should be within 70-90% of kills
        int minExpected = (int)(kills * 0.7);
        int maxExpected = (int)(kills * 0.9);
        
        assertTrue(deaths >= minExpected, 
            String.format("Deaths (%d) should be at least 70%% of kills (%d)", deaths, minExpected));
        assertTrue(deaths <= maxExpected, 
            String.format("Deaths (%d) should be at most 90%% of kills (%d)", deaths, maxExpected));
    }

    // Helper methods to invoke private methods using reflection
    private String invokeCalculateDuration(int kills) {
        return (String) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculateDuration", kills);
    }

    private int invokeCalculateGoodPlays(int kills) {
        return (Integer) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculateGoodPlays", kills);
    }

    private int invokeCalculateBadPlays(int kills) {
        return (Integer) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculateBadPlays", kills);
    }

    private double invokeCalculateUnifiedScore(int kills, int deaths, int goodPlays, int badPlays) {
        return (Double) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculateUnifiedScore", kills, deaths, goodPlays, badPlays);
    }

    private int invokeCalculateDeaths(int kills) {
        return (Integer) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculateDeaths", kills);
    }
    
    // ============================================
    // Tests para calculatePlaysFromPredictions con predicted_label
    // ============================================
    
    @Test
    void testCalculatePlaysFromPredictions_AllGoodDecisions() {
        // Given - All predictions are "good_decision"
        Map<String, Object> mlResponse = createMockMLResponse(10, "good_decision");
        
        // When
        int[] plays = invokeCalculatePlaysFromPredictions(mlResponse);
        
        // Then
        assertEquals(10, plays[0], "All 10 predictions should be counted as goodPlays");
        assertEquals(0, plays[1], "No predictions should be counted as badPlays");
    }
    
    @Test
    void testCalculatePlaysFromPredictions_AllOther() {
        // Given - All predictions are "other"
        Map<String, Object> mlResponse = createMockMLResponse(10, "other");
        
        // When
        int[] plays = invokeCalculatePlaysFromPredictions(mlResponse);
        
        // Then
        assertEquals(0, plays[0], "No predictions should be counted as goodPlays");
        assertEquals(10, plays[1], "All 10 predictions should be counted as badPlays");
    }
    
    @Test
    void testCalculatePlaysFromPredictions_MixedLabels() {
        // Given - Mixed predictions
        Map<String, Object> mlResponse = new HashMap<>();
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        // 5 good_decision, 3 good_positioning, 2 precise, 5 other
        for (int i = 0; i < 5; i++) {
            predictions.add(createPrediction("good_decision"));
        }
        for (int i = 0; i < 3; i++) {
            predictions.add(createPrediction("good_positioning"));
        }
        for (int i = 0; i < 2; i++) {
            predictions.add(createPrediction("precise"));
        }
        for (int i = 0; i < 5; i++) {
            predictions.add(createPrediction("other"));
        }
        
        mlResponse.put("predictions", predictions);
        
        // When
        int[] plays = invokeCalculatePlaysFromPredictions(mlResponse);
        
        // Then - good_decision, good_positioning, and precise are all "good"
        assertEquals(10, plays[0], "5 good_decision + 3 good_positioning + 2 precise = 10 goodPlays");
        assertEquals(5, plays[1], "5 other = 5 badPlays");
    }
    
    @Test
    void testCalculatePlaysFromPredictions_NullPredictions() {
        // Given
        Map<String, Object> mlResponse = new HashMap<>();
        mlResponse.put("predictions", null);
        
        // When
        int[] plays = invokeCalculatePlaysFromPredictions(mlResponse);
        
        // Then
        assertEquals(0, plays[0], "Should return 0 goodPlays for null predictions");
        assertEquals(0, plays[1], "Should return 0 badPlays for null predictions");
    }
    
    @Test
    void testIsGoodPlayFromLabel_ConsistencyWithPreloadedDataService() {
        // Verify that the logic is consistent across services
        assertTrue(PreloadedDataService.isGoodPlayFromLabel("good_decision"));
        assertTrue(PreloadedDataService.isGoodPlayFromLabel("good_positioning"));
        assertTrue(PreloadedDataService.isGoodPlayFromLabel("precise"));
        assertFalse(PreloadedDataService.isGoodPlayFromLabel("other"));
        assertFalse(PreloadedDataService.isGoodPlayFromLabel(null));
        assertFalse(PreloadedDataService.isGoodPlayFromLabel(""));
    }
    
    // Helper methods for new tests
    private Map<String, Object> createMockMLResponse(int count, String predictedLabel) {
        Map<String, Object> mlResponse = new HashMap<>();
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            predictions.add(createPrediction(predictedLabel));
        }
        
        mlResponse.put("predictions", predictions);
        return mlResponse;
    }
    
    private Map<String, Object> createPrediction(String predictedLabel) {
        Map<String, Object> prediction = new HashMap<>();
        Map<String, Object> predictionResult = new HashMap<>();
        predictionResult.put("predicted_label", predictedLabel);
        prediction.put("prediction", predictionResult);
        return prediction;
    }
    
    private int[] invokeCalculatePlaysFromPredictions(Map<String, Object> mlResponse) {
        return (int[]) ReflectionTestUtils.invokeMethod(databaseMatchService, "calculatePlaysFromPredictions", mlResponse);
    }
}
