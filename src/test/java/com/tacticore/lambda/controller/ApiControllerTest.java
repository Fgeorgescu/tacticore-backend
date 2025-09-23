package com.tacticore.lambda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.service.AnalyticsService;
import com.tacticore.lambda.service.ChatService;
import com.tacticore.lambda.service.DatabaseMatchService;
import com.tacticore.lambda.service.GameDataService;
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
        when(analyticsService.getDashboardStats()).thenReturn(
            new com.tacticore.lambda.model.DashboardStats(5, 100, 80, 30, 10, 7.5, 1.25)
        );

        // When & Then
        mockMvc.perform(get("/api/analytics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalMatches").value(5))
                .andExpect(jsonPath("$.totalKills").value(100))
                .andExpect(jsonPath("$.totalDeaths").value(80))
                .andExpect(jsonPath("$.totalGoodPlays").value(30))
                .andExpect(jsonPath("$.totalBadPlays").value(10))
                .andExpect(jsonPath("$.averageScore").value(7.5))
                .andExpect(jsonPath("$.kdr").value(1.25));
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
}

