package com.tacticore.lambda.service;

import com.tacticore.lambda.model.AnalyticsData;
import com.tacticore.lambda.model.AnalyticsDataEntity;
import com.tacticore.lambda.model.DashboardStats;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.repository.AnalyticsDataRepository;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsDataRepository analyticsDataRepository;

    @Mock
    private KillRepository killRepository;

    @Mock
    private MatchRepository matchRepository;
    
    @Mock
    private DatabaseMatchService databaseMatchService;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        // Setup common mock behaviors
    }

    @Test
    void testGetHistoricalAnalytics_AllTimeRange() {
        // Given
        List<AnalyticsDataEntity> entities = Arrays.asList(
            new AnalyticsDataEntity(LocalDate.of(2024, 1, 1), 18, 22, 0.82, 6.5, 4, 8, 1),
            new AnalyticsDataEntity(LocalDate.of(2024, 1, 3), 21, 19, 1.11, 7.2, 6, 6, 2)
        );
        when(analyticsDataRepository.findAllByOrderByDateAsc()).thenReturn(entities);

        // When
        List<AnalyticsData> result = analyticsService.getHistoricalAnalytics("all", "kdr");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());
        assertEquals(18, result.get(0).getKills());
        assertEquals(22, result.get(0).getDeaths());
        assertEquals(0.82, result.get(0).getKdr(), 0.01);
        
        verify(analyticsDataRepository).findAllByOrderByDateAsc();
    }

    @Test
    void testGetHistoricalAnalytics_WeekTimeRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusWeeks(1);
        LocalDate endDate = LocalDate.now();
        List<AnalyticsDataEntity> entities = Arrays.asList(
            new AnalyticsDataEntity(LocalDate.now().minusDays(3), 15, 12, 1.25, 7.8, 5, 3, 1)
        );
        when(analyticsDataRepository.findByDateBetween(startDate, endDate)).thenReturn(entities);

        // When
        List<AnalyticsData> result = analyticsService.getHistoricalAnalytics("week", "kdr");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(analyticsDataRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    void testGetDashboardStats() {
        // Given
        when(killRepository.count()).thenReturn(143L);
        when(matchRepository.count()).thenReturn(6L);
        when(killRepository.findAllAttackers()).thenReturn(Arrays.asList("player1", "player2"));
        when(killRepository.countKillsByUser("player1")).thenReturn(20L);
        when(killRepository.countDeathsByUser("player1")).thenReturn(15L);
        when(killRepository.countKillsByUser("player2")).thenReturn(18L);
        when(killRepository.countDeathsByUser("player2")).thenReturn(12L);
        
        // Mock MatchDto objects for score calculation
        com.tacticore.lambda.model.dto.MatchDto match1 = new com.tacticore.lambda.model.dto.MatchDto();
        match1.setKills(20);
        match1.setScore(7.5); // Mock score
        com.tacticore.lambda.model.dto.MatchDto match2 = new com.tacticore.lambda.model.dto.MatchDto();
        match2.setKills(18);
        match2.setScore(6.8); // Mock score
        when(databaseMatchService.getAllMatches()).thenReturn(Arrays.asList(match1, match2));

        // When
        DashboardStats result = analyticsService.getDashboardStats(null);

        // Then
        assertNotNull(result);
        assertEquals(6, result.getTotalMatches()); // El mock devuelve 6 matches
        assertEquals(143, result.getTotalKills()); // El mock devuelve 143 kills
        assertEquals(143, result.getTotalDeaths()); // Ahora las deaths son iguales a kills para todos los usuarios
        assertTrue(result.getKdr() > 0);
        assertTrue(result.getAverageScore() > 0);
        
        verify(killRepository).count();
        verify(matchRepository).count();
    }

    @Test
    void testGetDashboardStatsWithUser() {
        // Given
        String user = "flameZ";
        when(killRepository.countKillsByUser(user)).thenReturn(11L);
        when(killRepository.countDeathsByUser(user)).thenReturn(18L);
        when(databaseMatchService.getMatchesByUser(user)).thenReturn(Arrays.asList(
            new MatchDto("match1", "demo1.dem", false, "Dust2", "Ranked", 11, 18, 3, 1, "45:30", 1.42, java.time.LocalDateTime.of(2025, 1, 15, 10, 30))
        ));

        // When
        DashboardStats result = analyticsService.getDashboardStats(user);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalMatches());
        assertEquals(11, result.getTotalKills());
        assertEquals(18, result.getTotalDeaths()); // Ahora las deaths se calculan correctamente por usuario
        assertTrue(result.getKdr() > 0);
        assertTrue(result.getAverageScore() > 0);
        
        verify(killRepository, times(2)).countKillsByUser(user); // Se llama 2 veces: línea 62 y 74
        verify(killRepository, times(2)).countDeathsByUser(user); // Se llama 2 veces: línea 75 y 101
        verify(databaseMatchService, times(2)).getMatchesByUser(user); // Se llama 2 veces: línea 63 y 112
    }

    @Test
    void testAddAnalyticsData() {
        // Given
        AnalyticsData inputData = new AnalyticsData(
            LocalDate.of(2024, 1, 15), 25, 18, 1.39, 8.2, 8, 3, 1
        );
        AnalyticsDataEntity savedEntity = new AnalyticsDataEntity(
            LocalDate.of(2024, 1, 15), 25, 18, 1.39, 8.2, 8, 3, 1
        );
        when(analyticsDataRepository.save(any(AnalyticsDataEntity.class))).thenReturn(savedEntity);

        // When
        AnalyticsDataEntity result = analyticsService.addAnalyticsData(inputData);

        // Then
        assertNotNull(result);
        assertEquals(inputData.getDate(), result.getDate());
        assertEquals(inputData.getKills(), result.getKills());
        assertEquals(inputData.getDeaths(), result.getDeaths());
        assertEquals(inputData.getKdr(), result.getKdr(), 0.01);
        
        verify(analyticsDataRepository).save(any(AnalyticsDataEntity.class));
    }
}
