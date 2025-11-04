package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.dto.MatchDto;
import com.tacticore.lambda.service.AnalyticsService;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.GameDataService;
import com.tacticore.lambda.service.JsonMatchService;
import com.tacticore.lambda.service.KillAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseMatchService databaseMatchService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private KillAnalysisService killAnalysisService;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private GameDataService gameDataService;

    @MockBean
    private JsonMatchService jsonMatchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMaps() throws Exception {
        // Given
        List<String> expectedMaps = Arrays.asList("Dust2", "Mirage", "Inferno");
        when(gameDataService.getActiveMapNames()).thenReturn(expectedMaps);

        // When & Then
        mockMvc.perform(get("/api/maps"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("Dust2"))
                .andExpect(jsonPath("$[1]").value("Mirage"))
                .andExpect(jsonPath("$[2]").value("Inferno"));
    }

    @Test
    void testGetWeapons() throws Exception {
        // Given
        List<String> expectedWeapons = Arrays.asList("AK-47", "M4A4", "AWP");
        when(gameDataService.getActiveWeaponNames()).thenReturn(expectedWeapons);

        // When & Then
        mockMvc.perform(get("/api/weapons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("AK-47"))
                .andExpect(jsonPath("$[1]").value("M4A4"))
                .andExpect(jsonPath("$[2]").value("AWP"));
    }

    @Test
    void testGetDashboardStats() throws Exception {
        // Given
        when(analyticsService.getDashboardStats(null)).thenReturn(
            new com.tacticore.lambda.model.DashboardStats(2, 148, 148, 44, 14, 6.59, 1.06)
        );

        // When & Then
        mockMvc.perform(get("/api/analytics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalMatches").value(2))
                .andExpect(jsonPath("$.totalKills").value(148))
                .andExpect(jsonPath("$.totalDeaths").value(148))
                .andExpect(jsonPath("$.totalGoodPlays").value(44))
                .andExpect(jsonPath("$.totalBadPlays").value(14))
                .andExpect(jsonPath("$.averageScore").value(6.59))
                .andExpect(jsonPath("$.kdr").value(1.06));
    }

    @Test
    void testGetDashboardStatsWithUser() throws Exception {
        // Given
        when(analyticsService.getDashboardStats("flameZ")).thenReturn(
            new com.tacticore.lambda.model.DashboardStats(1, 11, 18, 3, 1, 3.05, 0.61)
        );

        // When & Then
        mockMvc.perform(get("/api/analytics/dashboard").param("user", "flameZ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalMatches").value(1))
                .andExpect(jsonPath("$.totalKills").value(11))
                .andExpect(jsonPath("$.totalDeaths").value(18))
                .andExpect(jsonPath("$.totalGoodPlays").value(3))
                .andExpect(jsonPath("$.totalBadPlays").value(1))
                .andExpect(jsonPath("$.averageScore").value(3.05))
                .andExpect(jsonPath("$.kdr").value(0.61));
    }

    @Test
    void testGetHistoricalAnalytics() throws Exception {
        // Given
        List<com.tacticore.lambda.model.AnalyticsData> expectedData = Arrays.asList(
            new com.tacticore.lambda.model.AnalyticsData(
                java.time.LocalDate.of(2024, 1, 1), 18, 22, 0.82, 6.5, 4, 8, 1
            ),
            new com.tacticore.lambda.model.AnalyticsData(
                java.time.LocalDate.of(2024, 1, 3), 21, 19, 1.11, 7.2, 6, 6, 2
            )
        );
        when(analyticsService.getHistoricalAnalytics("all", "kdr")).thenReturn(expectedData);

        // When & Then
        mockMvc.perform(get("/api/analytics/historical")
                .param("timeRange", "all")
                .param("metric", "kdr"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$.data[0].kills").value(18))
                .andExpect(jsonPath("$.data[0].deaths").value(22))
                .andExpect(jsonPath("$.data[0].kdr").value(0.82))
                .andExpect(jsonPath("$.data[1].date").value("2024-01-03"))
                .andExpect(jsonPath("$.data[1].kills").value(21))
                .andExpect(jsonPath("$.data[1].deaths").value(19))
                .andExpect(jsonPath("$.data[1].kdr").value(1.11));
    }

    @Test
    void testGetHistoricalAnalyticsWithDefaultParams() throws Exception {
        // Given
        List<com.tacticore.lambda.model.AnalyticsData> expectedData = Arrays.asList(
            new com.tacticore.lambda.model.AnalyticsData(
                java.time.LocalDate.of(2024, 1, 1), 18, 22, 0.82, 6.5, 4, 8, 1
            )
        );
        when(analyticsService.getHistoricalAnalytics("all", "kdr")).thenReturn(expectedData);

        // When & Then
        mockMvc.perform(get("/api/analytics/historical"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void testGetMatchesWithUser() throws Exception {
        // Given
        List<MatchDto> expectedMatches = Arrays.asList(
            new MatchDto("match1", "demo1.dem", false, "Dust2", "Ranked", 11, 18, 3, 1, "45:30", 1.42, java.time.LocalDateTime.of(2025, 1, 15, 10, 30))
        );
        when(databaseMatchService.getMatchesByUser("flameZ")).thenReturn(expectedMatches);

        // When & Then
        mockMvc.perform(get("/api/matches").param("user", "flameZ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matches").isArray())
                .andExpect(jsonPath("$.matches.length()").value(1))
                .andExpect(jsonPath("$.filteredBy").value("flameZ"))
                .andExpect(jsonPath("$.matches[0].id").value("match1"))
                .andExpect(jsonPath("$.matches[0].kills").value(11))
                .andExpect(jsonPath("$.matches[0].deaths").value(18))
                .andExpect(jsonPath("$.matches[0].goodPlays").value(3))
                .andExpect(jsonPath("$.matches[0].badPlays").value(1));
    }
}

