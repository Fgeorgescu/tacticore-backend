package com.tacticore.lambda.service;

import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.dto.KillAnalysisDto;
import com.tacticore.lambda.model.dto.PlayerStatsDto;
import com.tacticore.lambda.model.dto.RoundAnalysisDto;
import com.tacticore.lambda.repository.KillPredictionRepository;
import com.tacticore.lambda.repository.KillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KillAnalysisServiceTest {

    @Mock
    private KillRepository killRepository;

    @Mock
    private KillPredictionRepository killPredictionRepository;

    @InjectMocks
    private KillAnalysisService killAnalysisService;

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    void testGetOverallAnalysis() {
        // Given
        when(killRepository.getTotalKills()).thenReturn(100L);
        when(killRepository.getTotalHeadshots()).thenReturn(50L);
        when(killRepository.getAverageDistance()).thenReturn(500.0);
        when(killRepository.getAverageTimeInRound()).thenReturn(45.5);
        when(killRepository.getWeaponUsageStats()).thenReturn(Arrays.asList(
            new Object[]{"ak47", 30L},
            new Object[]{"m4a4", 20L}
        ));
        when(killRepository.getLocationStats()).thenReturn(Arrays.asList(
            new Object[]{"BombsiteA", 40L},
            new Object[]{"BombsiteB", 35L}
        ));
        when(killRepository.getKillsPerRound()).thenReturn(Arrays.asList(
            new Object[]{1, 5L},
            new Object[]{2, 7L}
        ));
        when(killRepository.getKillsBySide()).thenReturn(Arrays.asList(
            new Object[]{"ct", 55L},
            new Object[]{"t", 45L}
        ));
        when(killRepository.findAll()).thenReturn(Arrays.asList(createMockKill("player1", "player2")));
        when(killPredictionRepository.getTopPredictionStats()).thenReturn(Arrays.asList(
            new Object[]{"good_play", 60L},
            new Object[]{"bad_play", 40L}
        ));
        when(killPredictionRepository.getAverageConfidenceByLabel()).thenReturn(Arrays.asList(
            new Object[]{"good_play", 0.85},
            new Object[]{"bad_play", 0.65}
        ));

        // When
        KillAnalysisDto result = killAnalysisService.getOverallAnalysis();

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getTotalKills());
        assertEquals(50L, result.getTotalHeadshots());
        assertEquals(50.0, result.getHeadshotRate(), 0.1);
        assertEquals(500.0, result.getAverageDistance(), 0.1);
        assertEquals(45.5, result.getAverageTimeInRound(), 0.1);
        assertNotNull(result.getWeaponStats());
        assertNotNull(result.getLocationStats());
        assertNotNull(result.getRoundStats());
        assertNotNull(result.getSideStats());
        assertNotNull(result.getTopPlayers());
        assertNotNull(result.getPredictionStats());
    }

    @Test
    void testGetOverallAnalysis_NoKills() {
        // Given
        when(killRepository.getTotalKills()).thenReturn(0L);
        when(killRepository.getTotalHeadshots()).thenReturn(0L);
        when(killRepository.getAverageDistance()).thenReturn(0.0);
        when(killRepository.getAverageTimeInRound()).thenReturn(0.0);
        when(killRepository.getWeaponUsageStats()).thenReturn(Collections.emptyList());
        when(killRepository.getLocationStats()).thenReturn(Collections.emptyList());
        when(killRepository.getKillsPerRound()).thenReturn(Collections.emptyList());
        when(killRepository.getKillsBySide()).thenReturn(Collections.emptyList());
        when(killRepository.findAll()).thenReturn(Collections.emptyList());
        when(killPredictionRepository.getTopPredictionStats()).thenReturn(Collections.emptyList());
        when(killPredictionRepository.getAverageConfidenceByLabel()).thenReturn(Collections.emptyList());

        // When
        KillAnalysisDto result = killAnalysisService.getOverallAnalysis();

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalKills());
        assertEquals(0.0, result.getHeadshotRate(), 0.1);
    }

    @Test
    void testGetPlayerStats() {
        // Given
        String playerName = "testPlayer";
        when(killRepository.countKillsByAttacker(playerName)).thenReturn(25L);
        when(killRepository.countDeathsByVictim(playerName)).thenReturn(15L);
        when(killRepository.countHeadshotsByAttacker(playerName)).thenReturn(12L);
        
        KillEntity kill1 = createMockKill(playerName, "victim1");
        kill1.setDistance(500.0);
        kill1.setWeapon("ak47");
        KillEntity kill2 = createMockKill(playerName, "victim2");
        kill2.setDistance(700.0);
        kill2.setWeapon("ak47");
        KillEntity kill3 = createMockKill(playerName, "victim3");
        kill3.setDistance(300.0);
        kill3.setWeapon("awp");
        
        when(killRepository.findByAttacker(playerName)).thenReturn(Arrays.asList(kill1, kill2, kill3));

        // When
        PlayerStatsDto result = killAnalysisService.getPlayerStats(playerName);

        // Then
        assertNotNull(result);
        assertEquals(playerName, result.getPlayerName());
        assertEquals(25L, result.getKills());
        assertEquals(15L, result.getDeaths());
        assertEquals(1.67, result.getKdRatio(), 0.1);
        assertEquals(12L, result.getHeadshots());
        assertEquals(48.0, result.getHeadshotRate(), 0.1);
        assertEquals(500.0, result.getAverageDistance(), 1.0);
        assertEquals("ak47", result.getFavoriteWeapon());
        assertTrue(result.getPerformanceScore() > 0);
    }

    @Test
    void testGetPlayerStats_NoDeaths() {
        // Given
        String playerName = "testPlayer";
        when(killRepository.countKillsByAttacker(playerName)).thenReturn(10L);
        when(killRepository.countDeathsByVictim(playerName)).thenReturn(0L);
        when(killRepository.countHeadshotsByAttacker(playerName)).thenReturn(5L);
        when(killRepository.findByAttacker(playerName)).thenReturn(Arrays.asList(createMockKill(playerName, "victim1")));

        // When
        PlayerStatsDto result = killAnalysisService.getPlayerStats(playerName);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getKills());
        assertEquals(0L, result.getDeaths());
        assertEquals(10.0, result.getKdRatio(), 0.1);
    }

    @Test
    void testGetRoundAnalysis() {
        // Given
        Integer roundNumber = 1;
        KillEntity kill1 = createMockKill("player1", "victim1");
        kill1.setRound(1);
        kill1.setTimeInRound(30.5);
        kill1.setPlace("BombsiteA");
        kill1.setWeapon("ak47");
        kill1.setSide("ct");
        kill1.setHeadshot(true);
        kill1.setDistance(500.0);
        
        KillEntity kill2 = createMockKill("player1", "victim2");
        kill2.setRound(1);
        kill2.setTimeInRound(45.0);
        kill2.setPlace("BombsiteA");
        kill2.setWeapon("ak47");
        kill2.setSide("ct");
        kill2.setHeadshot(false);
        kill2.setDistance(600.0);
        
        when(killRepository.findByRound(roundNumber)).thenReturn(Arrays.asList(kill1, kill2));

        // When
        RoundAnalysisDto result = killAnalysisService.getRoundAnalysis(roundNumber);

        // Then
        assertNotNull(result);
        assertEquals(roundNumber, result.getRoundNumber());
        assertEquals(2L, result.getTotalKills());
        assertEquals(45.0, result.getDuration(), 0.1);
        assertEquals("player1", result.getMostActivePlayer());
        assertNotNull(result.getHotSpots());
        assertNotNull(result.getWeaponDistribution());
        assertNotNull(result.getCtTBalance());
        assertEquals(50.0, result.getHeadshotRate(), 0.1);
        assertEquals(550.0, result.getAverageDistance(), 1.0);
    }

    @Test
    void testGetRoundAnalysis_EmptyRound() {
        // Given
        Integer roundNumber = 99;
        when(killRepository.findByRound(roundNumber)).thenReturn(Collections.emptyList());

        // When
        RoundAnalysisDto result = killAnalysisService.getRoundAnalysis(roundNumber);

        // Then
        assertNotNull(result);
        assertEquals(roundNumber, result.getRoundNumber());
        assertEquals(0L, result.getTotalKills());
        assertEquals(0.0, result.getDuration(), 0.1);
        assertEquals("Unknown", result.getMostActivePlayer());
    }

    @Test
    void testGetAnalysisByUser() {
        // Given
        String user = "testUser";
        when(killRepository.countKillsByUser(user)).thenReturn(20L);
        when(killRepository.countHeadshotsByUser(user)).thenReturn(10L);
        when(killRepository.getAverageDistanceByUser(user)).thenReturn(450.0);
        when(killRepository.getAverageTimeInRoundByUser(user)).thenReturn(40.0);
        List<Object[]> weaponStats = new ArrayList<>();
        weaponStats.add(new Object[]{"ak47", 12L});
        when(killRepository.getWeaponUsageStatsByUser(user)).thenReturn(weaponStats);
        
        List<Object[]> locationStats = new ArrayList<>();
        locationStats.add(new Object[]{"BombsiteA", 8L});
        when(killRepository.getLocationStatsByUser(user)).thenReturn(locationStats);
        
        List<Object[]> roundStats = new ArrayList<>();
        roundStats.add(new Object[]{1, 5L});
        when(killRepository.getKillsPerRoundByUser(user)).thenReturn(roundStats);
        
        List<Object[]> sideStats = new ArrayList<>();
        sideStats.add(new Object[]{"ct", 12L});
        when(killRepository.getKillsBySideByUser(user)).thenReturn(sideStats);
        when(killRepository.countKillsByAttacker(user)).thenReturn(20L);
        when(killRepository.countDeathsByVictim(user)).thenReturn(15L);
        when(killRepository.countHeadshotsByAttacker(user)).thenReturn(10L);
        when(killRepository.findByAttacker(user)).thenReturn(Arrays.asList(createMockKill(user, "victim1")));

        // When
        KillAnalysisDto result = killAnalysisService.getAnalysisByUser(user);

        // Then
        assertNotNull(result);
        assertEquals(20L, result.getTotalKills());
        assertEquals(10L, result.getTotalHeadshots());
        assertEquals(50.0, result.getHeadshotRate(), 0.1);
        assertEquals(450.0, result.getAverageDistance(), 0.1);
        assertEquals(40.0, result.getAverageTimeInRound(), 0.1);
        assertNotNull(result.getWeaponStats());
        assertNotNull(result.getLocationStats());
        assertNotNull(result.getRoundStats());
        assertNotNull(result.getSideStats());
        assertNotNull(result.getTopPlayers());
    }

    @Test
    void testGetKillsByUser() {
        // Given
        String user = "testUser";
        KillEntity kill1 = createMockKill(user, "victim1");
        KillEntity kill2 = createMockKill(user, "victim2");
        when(killRepository.findByUser(user)).thenReturn(Arrays.asList(kill1, kill2));

        // When
        List<KillEntity> result = killAnalysisService.getKillsByUser(user);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(killRepository).findByUser(user);
    }

    @Test
    void testGetKillsByUserAndRound() {
        // Given
        String user = "testUser";
        Integer round = 1;
        KillEntity kill1 = createMockKill(user, "victim1");
        kill1.setRound(1);
        when(killRepository.findByUserAndRound(user, round)).thenReturn(Arrays.asList(kill1));

        // When
        List<KillEntity> result = killAnalysisService.getKillsByUserAndRound(user, round);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(killRepository).findByUserAndRound(user, round);
    }

    @Test
    void testGetAllKills() {
        // Given
        KillEntity kill1 = createMockKill("player1", "victim1");
        KillEntity kill2 = createMockKill("player2", "victim2");
        when(killRepository.findAll()).thenReturn(Arrays.asList(kill1, kill2));

        // When
        List<KillEntity> result = killAnalysisService.getAllKills();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(killRepository).findAll();
    }

    @Test
    void testGetAllUsers() {
        // Given
        when(killRepository.findAllAttackers()).thenReturn(Arrays.asList("player1", "player2"));
        when(killRepository.findAllVictims()).thenReturn(Arrays.asList("player2", "player3"));

        // When
        List<String> result = killAnalysisService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("player1"));
        assertTrue(result.contains("player2"));
        assertTrue(result.contains("player3"));
        verify(killRepository).findAllAttackers();
        verify(killRepository).findAllVictims();
    }

    @Test
    void testGetTopPlayers() {
        // Given
        KillEntity kill1 = createMockKill("player1", "victim1");
        KillEntity kill2 = createMockKill("player1", "victim2");
        KillEntity kill3 = createMockKill("player2", "victim1");
        KillEntity kill4 = createMockKill("victim1", "player1"); // Death for player1
        
        when(killRepository.findAll()).thenReturn(Arrays.asList(kill1, kill2, kill3, kill4));

        // When
        List<Map<String, Object>> result = killAnalysisService.getTopPlayers();

        // Then
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.size() <= 10);
        
        // Verificar que está ordenado por KDR descendente
        for (int i = 0; i < result.size() - 1; i++) {
            Double kd1 = (Double) result.get(i).get("kd_ratio");
            Double kd2 = (Double) result.get(i + 1).get("kd_ratio");
            assertTrue(kd1 >= kd2);
        }
    }

    @Test
    void testGetPredictionStats() {
        // Given
        when(killPredictionRepository.getTopPredictionStats()).thenReturn(Arrays.asList(
            new Object[]{"good_play", 75L},
            new Object[]{"bad_play", 25L}
        ));
        when(killPredictionRepository.getAverageConfidenceByLabel()).thenReturn(Arrays.asList(
            new Object[]{"good_play", 0.90},
            new Object[]{"bad_play", 0.70}
        ));

        // When
        List<Map<String, Object>> result = killAnalysisService.getPredictionStats();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("good_play", result.get(0).get("label"));
        assertEquals(75L, result.get(0).get("count"));
        assertEquals(0.90, result.get(0).get("average_confidence"));
    }

    // Helper method to create mock KillEntity
    private KillEntity createMockKill(String attacker, String victim) {
        KillEntity kill = new KillEntity();
        kill.setAttacker(attacker);
        kill.setVictim(victim);
        kill.setWeapon("ak47");
        kill.setRound(1);
        kill.setTimeInRound(30.0);
        kill.setPlace("BombsiteA");
        kill.setSide("ct");
        kill.setHeadshot(false);
        kill.setDistance(500.0);
        return kill;
    }
}

