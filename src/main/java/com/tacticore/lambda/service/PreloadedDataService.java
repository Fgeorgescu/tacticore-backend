package com.tacticore.lambda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.ChatMessageEntity;
import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.repository.ChatMessageRepository;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PreloadedDataService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private KillRepository killRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void loadPreloadedData() {
        loadPreloadedMatches();
        loadPreloadedChatMessages();
    }
    
    private void loadPreloadedMatches() {
        // Verificar si ya existen matches precargados
        if (matchRepository.count() > 0) {
            return; // Ya hay datos cargados
        }
        
        List<MatchEntity> preloadedMatches = Arrays.asList(
            new MatchEntity(
                "1", 
                "dust2_ranked_2024.dem", 
                "Dust2", 
                64, 
                24, 
                "processed", 
                true
            ),
            new MatchEntity(
                "2", 
                "mirage_casual_2024.dem", 
                "Mirage", 
                64, 
                16, 
                "processed", 
                false
            )
        );
        
        matchRepository.saveAll(preloadedMatches);
        System.out.println("Matches precargados: " + preloadedMatches.size());
    }
    
    private void loadPreloadedChatMessages() {
        // Verificar si ya existen mensajes precargados
        if (chatMessageRepository.count() > 0) {
            return; // Ya hay datos cargados
        }
        
        List<ChatMessageEntity> preloadedMessages = Arrays.asList(
            // Mensaje de bienvenida para match 1
            new ChatMessageEntity(
                "1", 
                "Bot", 
                "Si tienes una duda, podes realizarme cualquier consulta", 
                LocalDateTime.of(2024, 1, 15, 14, 30)
            ),
            
            // Mensaje de bienvenida para match 2
            new ChatMessageEntity(
                "2", 
                "Bot", 
                "Si tienes una duda, podes realizarme cualquier consulta", 
                LocalDateTime.of(2024, 1, 14, 16, 20)
            )
        );
        
        chatMessageRepository.saveAll(preloadedMessages);
        System.out.println("Mensajes de chat precargados: " + preloadedMessages.size());
    }
    
    public void loadTestMatch() {
        // Verificar si ya existe la partida de prueba
        if (matchRepository.existsByMatchId("test_match")) {
            return; // Ya existe
        }
        
        // Crear partida de prueba
        MatchEntity testMatch = new MatchEntity(
            "test_match", 
            "test_demo.dem", 
            "Dust2", 
            64, 
            25, 
            "completed", 
            true
        );
        
        matchRepository.save(testMatch);
        
        // Crear mensaje de bienvenida del Bot para la partida de prueba
        ChatMessageEntity welcomeMessage = new ChatMessageEntity(
            "test_match",
            "Bot",
            "Si tienes una duda, podes realizarme cualquier consulta",
            LocalDateTime.of(2024, 1, 15, 14, 30)
        );
        chatMessageRepository.save(welcomeMessage);
        
        System.out.println("Partida de prueba creada: " + testMatch.getMatchId());
    }
    
    public void loadInfernoMatch() {
        // Verificar si ya existe la partida de Inferno
        if (matchRepository.existsByMatchId("inferno_demo")) {
            return; // Ya existe
        }
        
        // Crear partida de Inferno con datos reales
        MatchEntity infernoMatch = new MatchEntity(
            "inferno_demo", 
            "inferno1.dem", 
            "de_inferno", 
            64, 
            107, 
            "completed", 
            false
        );
        
        matchRepository.save(infernoMatch);
        
        // Crear mensaje de bienvenida del Bot para la partida de Inferno
        ChatMessageEntity welcomeMessage = new ChatMessageEntity(
            "inferno_demo",
            "Bot",
            "Partida de Inferno cargada. Analicemos las jugadas de este mapa clásico.",
            LocalDateTime.now()
        );
        chatMessageRepository.save(welcomeMessage);
        
        System.out.println("Partida de Inferno creada: " + infernoMatch.getMatchId());
        
        // Cargar kills de Inferno
        loadInfernoKills();
    }
    
    private void loadInfernoKills() {
        // Verificar si ya existen kills para la partida de Inferno
        if (killRepository.count() > 0) {
            return; // Ya hay kills cargados
        }
        
        try {
            // Cargar kills reales desde el archivo JSON
            File infernoFile = new File("demos-jsons/inferno1.json");
            if (!infernoFile.exists()) {
                System.err.println("Archivo inferno1.json no encontrado en demos-jsons/");
                return;
            }
            
            JsonNode rootNode = objectMapper.readTree(infernoFile);
            JsonNode predictionsNode = rootNode.path("predictions");
            
            if (predictionsNode.isArray()) {
                int loadedKills = 0;
                for (JsonNode killNode : predictionsNode) {
                    try {
                        KillEntity kill = createKillFromJson(killNode);
                        if (kill != null) {
                            killRepository.save(kill);
                            loadedKills++;
                        }
                    } catch (Exception e) {
                        System.err.println("Error cargando kill: " + e.getMessage());
                    }
                }
                System.out.println("Kills de Inferno cargados: " + loadedKills);
            }
            
        } catch (IOException e) {
            System.err.println("Error leyendo archivo inferno1.json: " + e.getMessage());
        }
    }
    
    private KillEntity createKillFromJson(JsonNode killNode) {
        try {
            KillEntity kill = new KillEntity();
            
            // Datos básicos del kill
            kill.setKillId(killNode.path("kill_id").asText());
            kill.setMatchId("inferno_demo");
            kill.setAttacker(killNode.path("attacker").asText());
            kill.setVictim(killNode.path("victim").asText());
            kill.setPlace(killNode.path("place").asText("Unknown"));
            kill.setRound(killNode.path("round").asInt());
            kill.setWeapon(killNode.path("weapon").asText());
            kill.setHeadshot(killNode.path("headshot").asBoolean(false));
            kill.setDistance(killNode.path("distance").asDouble(0.0));
            kill.setTimeInRound(killNode.path("time_in_round").asDouble(0.0));
            
            // Contexto del kill
            JsonNode contextNode = killNode.path("context");
            if (!contextNode.isMissingNode()) {
                kill.setKillTick(contextNode.path("kill_tick").asLong(0));
                kill.setSide(contextNode.path("side").asText("t"));
                kill.setAttackerX(contextNode.path("attacker_x").asDouble(0.0));
                kill.setAttackerY(contextNode.path("attacker_y").asDouble(0.0));
                kill.setAttackerZ(contextNode.path("attacker_z").asDouble(0.0));
                kill.setVictimX(contextNode.path("victim_x").asDouble(0.0));
                kill.setVictimY(contextNode.path("victim_y").asDouble(0.0));
                kill.setVictimZ(contextNode.path("victim_z").asDouble(0.0));
                kill.setAttackerHealth(contextNode.path("attacker_health").asDouble(100.0));
                kill.setVictimHealth(contextNode.path("victim_health").asDouble(100.0));
                kill.setFlashNear(contextNode.path("flash_near").asBoolean(false));
                kill.setSmokeNear(contextNode.path("smoke_near").asBoolean(false));
                kill.setMolotovNear(contextNode.path("molotov_near").asBoolean(false));
                kill.setHeNear(contextNode.path("he_near").asBoolean(false));
            }
            
            return kill;
            
        } catch (Exception e) {
            System.err.println("Error creando KillEntity desde JSON: " + e.getMessage());
            return null;
        }
    }
    
    public void clearPreloadedData() {
        chatMessageRepository.deleteAll();
        matchRepository.deleteAll();
        System.out.println("Datos precargados eliminados");
    }
}
