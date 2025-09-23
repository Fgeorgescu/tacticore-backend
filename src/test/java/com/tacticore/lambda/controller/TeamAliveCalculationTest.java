package com.tacticore.lambda.controller;

import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.service.KillAnalysisService;
import org.junit.jupiter.api.BeforeEach;
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
class TeamAliveCalculationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KillAnalysisService killAnalysisService;

    private List<KillEntity> testKills;

    @BeforeEach
    void setUp() {
        // Crear kills de prueba para simular una ronda
        KillEntity kill1 = new KillEntity();
        kill1.setKillId("kill1");
        kill1.setAttacker("player1");
        kill1.setVictim("player2");
        kill1.setRound(1);
        kill1.setTimeInRound(10.0);
        kill1.setSide("t"); // víctima del lado T
        kill1.setWeapon("ak47");
        kill1.setHeadshot(false);
        kill1.setDistance(100.0);
        kill1.setPlace("A Site");

        KillEntity kill2 = new KillEntity();
        kill2.setKillId("kill2");
        kill2.setAttacker("player3");
        kill2.setVictim("player4");
        kill2.setRound(1);
        kill2.setTimeInRound(15.0);
        kill2.setSide("ct"); // víctima del lado CT
        kill2.setWeapon("m4a4");
        kill2.setHeadshot(true);
        kill2.setDistance(200.0);
        kill2.setPlace("B Site");

        KillEntity kill3 = new KillEntity();
        kill3.setKillId("kill3");
        kill3.setAttacker("player5");
        kill3.setVictim("player6");
        kill3.setRound(2); // Nueva ronda
        kill3.setTimeInRound(5.0);
        kill3.setSide("t"); // víctima del lado T
        kill3.setWeapon("awp");
        kill3.setHeadshot(true);
        kill3.setDistance(500.0);
        kill3.setPlace("Mid");

        testKills = Arrays.asList(kill1, kill2, kill3);
    }

    @Test
    void testTeamAliveCalculationDecreasesCorrectly() throws Exception {
        // Given
        when(killAnalysisService.getAllKills()).thenReturn(testKills);

        // When & Then
        mockMvc.perform(get("/api/matches/test_match/kills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kills").isArray())
                .andExpect(jsonPath("$.kills.length()").value(3))
                .andExpect(jsonPath("$.matchId").value("test_match"))
                
                // Primer kill: T=4, CT=5 (player2 muere del lado T)
                .andExpect(jsonPath("$.kills[0].killer").value("player1"))
                .andExpect(jsonPath("$.kills[0].victim").value("player2"))
                .andExpect(jsonPath("$.kills[0].round").value(1))
                .andExpect(jsonPath("$.kills[0].teamAlive.t").value(4))
                .andExpect(jsonPath("$.kills[0].teamAlive.ct").value(5))
                
                // Segundo kill: T=4, CT=4 (player4 muere del lado CT)
                .andExpect(jsonPath("$.kills[1].killer").value("player3"))
                .andExpect(jsonPath("$.kills[1].victim").value("player4"))
                .andExpect(jsonPath("$.kills[1].round").value(1))
                .andExpect(jsonPath("$.kills[1].teamAlive.t").value(4))
                .andExpect(jsonPath("$.kills[1].teamAlive.ct").value(4))
                
                // Tercer kill: Nueva ronda, T=4, CT=5 (player6 muere del lado T)
                .andExpect(jsonPath("$.kills[2].killer").value("player5"))
                .andExpect(jsonPath("$.kills[2].victim").value("player6"))
                .andExpect(jsonPath("$.kills[2].round").value(2))
                .andExpect(jsonPath("$.kills[2].teamAlive.t").value(4))
                .andExpect(jsonPath("$.kills[2].teamAlive.ct").value(5));
    }

    @Test
    void testTeamAliveCalculationWithSameSideKills() throws Exception {
        // Given - Todos los kills son del mismo lado
        KillEntity kill1 = new KillEntity();
        kill1.setKillId("kill1");
        kill1.setAttacker("player1");
        kill1.setVictim("player2");
        kill1.setRound(1);
        kill1.setTimeInRound(10.0);
        kill1.setSide("t");
        kill1.setWeapon("ak47");
        kill1.setHeadshot(false);
        kill1.setDistance(100.0);
        kill1.setPlace("A Site");

        KillEntity kill2 = new KillEntity();
        kill2.setKillId("kill2");
        kill2.setAttacker("player3");
        kill2.setVictim("player4");
        kill2.setRound(1);
        kill2.setTimeInRound(15.0);
        kill2.setSide("t"); // Mismo lado
        kill2.setWeapon("ak47");
        kill2.setHeadshot(false);
        kill2.setDistance(100.0);
        kill2.setPlace("A Site");

        when(killAnalysisService.getAllKills()).thenReturn(Arrays.asList(kill1, kill2));

        // When & Then
        mockMvc.perform(get("/api/matches/same_side_match/kills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kills[0].teamAlive.t").value(4)) // Primer kill
                .andExpect(jsonPath("$.kills[0].teamAlive.ct").value(5))
                .andExpect(jsonPath("$.kills[1].teamAlive.t").value(3)) // Segundo kill
                .andExpect(jsonPath("$.kills[1].teamAlive.ct").value(5));
    }

    @Test
    void testTeamAliveCalculationWithUnknownSide() throws Exception {
        // Given - Kill con lado desconocido
        KillEntity kill1 = new KillEntity();
        kill1.setKillId("kill1");
        kill1.setAttacker("player1");
        kill1.setVictim("player2");
        kill1.setRound(1);
        kill1.setTimeInRound(10.0);
        kill1.setSide("unknown"); // Lado desconocido
        kill1.setWeapon("ak47");
        kill1.setHeadshot(false);
        kill1.setDistance(100.0);
        kill1.setPlace("A Site");

        when(killAnalysisService.getAllKills()).thenReturn(Arrays.asList(kill1));

        // When & Then - Debe mantener los valores iniciales
        mockMvc.perform(get("/api/matches/unknown_side_match/kills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kills[0].teamAlive.t").value(5))
                .andExpect(jsonPath("$.kills[0].teamAlive.ct").value(5));
    }

    @Test
    void testTeamAliveCalculationWithEmptyKills() throws Exception {
        // Given
        when(killAnalysisService.getAllKills()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/matches/empty_match/kills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kills").isArray())
                .andExpect(jsonPath("$.kills.length()").value(0))
                .andExpect(jsonPath("$.matchId").value("empty_match"));
    }
}
