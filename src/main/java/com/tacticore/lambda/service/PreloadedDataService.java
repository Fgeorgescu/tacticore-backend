package com.tacticore.lambda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.ChatMessageEntity;
import com.tacticore.lambda.model.KillEntity;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.repository.ChatMessageRepository;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import com.tacticore.lambda.repository.UserRepository;
import com.tacticore.lambda.model.UserEntity;
import com.tacticore.lambda.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PreloadedDataService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private KillRepository killRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void loadPreloadedData() {
        loadPreloadedMatches();
        loadPreloadedChatMessages();
    }
    
    private void loadPreloadedMatches() {
        // Verificar si ya existen matches precargados básicos
        if (matchRepository.count() > 0) {
            // Si ya hay matches, solo asegurarnos de que exista el match de prueba "processing_demo"
            if (!matchRepository.existsByMatchId("processing_demo")) {
                MatchEntity processingMatch = new MatchEntity(
                    "processing_demo", 
                    "nuke_processing.dem", 
                    "Unknown", 
                    null, 
                    null, 
                    "processing", 
                    false
                );
                matchRepository.save(processingMatch);
                System.out.println("Match de prueba 'processing_demo' agregado");
            }
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
            ),
            new MatchEntity(
                "processing_demo", 
                "nuke_processing.dem", 
                "Unknown", 
                null, 
                null, 
                "processing", 
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
            ),
            
            // Mensaje de bienvenida para match en procesamiento
            new ChatMessageEntity(
                "processing_demo", 
                "Bot", 
                "Esta partida se está procesando. Los datos estarán disponibles pronto.", 
                LocalDateTime.now()
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
    
    public void loadAllDemoMatches() {
        // Cargar las 4 partidas con datos reales
        loadInfernoMatch();
        loadMirageMatch();
        loadDust2Match();
        loadMirage2Match();
        
        // Actualizar estadísticas de todos los usuarios después de cargar todos los kills
        updateAllUserStatsFromKills();
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
                        KillEntity kill = createKillFromJson(killNode, "inferno_demo");
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
    
    private KillEntity createKillFromJson(JsonNode killNode, String matchId) {
        try {
            KillEntity kill = new KillEntity();
            
            // Datos básicos del kill - generar ID único incluyendo matchId
            String originalKillId = killNode.path("kill_id").asText();
            String uniqueKillId = matchId + "_" + originalKillId;
            kill.setKillId(uniqueKillId);
            kill.setMatchId(matchId);
            String attacker = mapUserName(killNode.path("attacker").asText());
            String victim = mapUserName(killNode.path("victim").asText());
            
            // Asegurar que los usuarios existan
            ensureUserExists(attacker);
            ensureUserExists(victim);
            
            kill.setAttacker(attacker);
            kill.setVictim(victim);
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
    
    public void loadMirageMatch() {
        loadDemoMatch("mirage_demo", "de_mirage.dem", "de_mirage", "demos-jsons/de_mirage.json", 143);
    }
    
    public void loadDust2Match() {
        loadDemoMatch("dust2_demo", "dust1.dem", "de_dust2", "demos-jsons/dust1.json", 120);
    }
    
    public void loadMirage2Match() {
        loadDemoMatch("mirage2_demo", "mirage1.dem", "de_mirage", "demos-jsons/mirage1.json", 148);
    }
    
    private void loadDemoMatch(String matchId, String fileName, String mapName, String jsonPath, int totalKills) {
        // Verificar si ya existe la partida
        if (matchRepository.existsByMatchId(matchId)) {
            return; // Ya existe
        }
        
        // Crear partida con datos reales
        MatchEntity match = new MatchEntity(
            matchId, 
            fileName, 
            mapName, 
            64, // tickrate
            totalKills, 
            "completed", 
            false // hasVideo
        );
        
        matchRepository.save(match);
        
        // Crear mensaje de bienvenida del Bot
        ChatMessageEntity welcomeMessage = new ChatMessageEntity(
            matchId,
            "Bot",
            "Partida de " + mapName + " cargada. Analicemos las jugadas de este mapa.",
            LocalDateTime.now()
        );
        chatMessageRepository.save(welcomeMessage);
        
        System.out.println("Partida creada: " + matchId + " (" + mapName + ")");
        
        // Cargar kills de esta partida
        loadKillsFromJson(matchId, jsonPath);
    }
    
    // Mapeo de usuarios de JSONs a usuarios preloaded
    private static final Map<String, String> USER_MAPPING = Map.of(
        // Inferno users -> preloaded users
        "malbsMd", "flameZ",
        "frozen", "Senzu", 
        // Dust2 users -> preloaded users
        "sh1ro", "sh1ro",  // Este ya coincide
        "nettik", "bLitz",
        // Mirage users -> preloaded users  
        "jcobbb", "ropz",
        "bLitz", "bLitz",  // Este ya coincide
        "ropz", "ropz"     // Este ya coincide
    );
    
    private String mapUserName(String originalName) {
        return USER_MAPPING.getOrDefault(originalName, originalName);
    }
    
    private void ensureUserExists(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return;
        }
        
        // Verificar si el usuario ya existe
        if (!userRepository.existsByName(userName)) {
            // Crear nuevo usuario con rol aleatorio
            UserEntity newUser = new UserEntity();
            newUser.setName(userName);
            newUser.setRole(getRandomRole()); // Rol aleatorio
            newUser.setAverageScore(5.0);
            newUser.setTotalKills(0);
            newUser.setTotalDeaths(0);
            newUser.setTotalMatches(0);
            
            userRepository.save(newUser);
            System.out.println("Usuario creado dinámicamente: " + userName + " con rol: " + newUser.getRole());
        }
    }
    
    // Get a random role from available roles
    private String getRandomRole() {
        UserRole[] roles = UserRole.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(roles.length);
        return roles[randomIndex].getDisplayName();
    }
    
    private void loadKillsFromJson(String matchId, String jsonPath) {
        try {
            // Cargar kills reales desde el archivo JSON
            File jsonFile = new File(jsonPath);
            if (!jsonFile.exists()) {
                System.err.println("Archivo " + jsonPath + " no encontrado");
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile);
            JsonNode predictionsNode = rootNode.path("predictions");
            
            if (predictionsNode.isArray()) {
                for (JsonNode predictionNode : predictionsNode) {
                    KillEntity kill = createKillFromJson(predictionNode, matchId);
                    if (kill != null) {
                        // El matchId ya se asigna en createKillFromJson
                        killRepository.save(kill);
                    }
                }
                System.out.println("Kills cargados para " + matchId + ": " + predictionsNode.size() + " kills");
            }
            
        } catch (IOException e) {
            System.err.println("Error leyendo archivo " + jsonPath + ": " + e.getMessage());
        }
    }

    public void clearPreloadedData() {
        chatMessageRepository.deleteAll();
        matchRepository.deleteAll();
        killRepository.deleteAll();
        System.out.println("Datos precargados eliminados");
    }
    
    public void reloadKillsWithMapping() {
        // Limpiar solo los kills para recargar con mapeo
        killRepository.deleteAll();
        System.out.println("Kills eliminados, recargando con mapeo de usuarios...");
        
        // Recargar kills con mapeo
        loadKillsFromJson("inferno_demo", "demos-jsons/inferno1.json");
        loadKillsFromJson("mirage_demo", "demos-jsons/de_mirage.json");
        loadKillsFromJson("dust2_demo", "demos-jsons/dust1.json");
        loadKillsFromJson("mirage2_demo", "demos-jsons/mirage1.json");
        
        System.out.println("Kills recargados con mapeo de usuarios exitosamente!");
        
        // Actualizar estadísticas de todos los usuarios después de recargar
        updateAllUserStatsFromKills();
    }
    
    /**
     * Actualiza las estadísticas de todos los usuarios basándose en los datos reales de kills
     */
    public void updateAllUserStatsFromKills() {
        System.out.println("Actualizando estadísticas de todos los usuarios desde kills...");
        
        // Obtener todos los usuarios únicos de los kills
        List<String> attackers = killRepository.findAllAttackers();
        List<String> victims = killRepository.findAllVictims();
        
        Set<String> allKillsUsers = new HashSet<>();
        allKillsUsers.addAll(attackers);
        allKillsUsers.addAll(victims);
        
        // Actualizar usuarios que tienen datos de kills
        for (String userName : allKillsUsers) {
            if (userName != null && !userName.trim().isEmpty()) {
                updateUserStatsFromKills(userName);
            }
        }
        
        // Actualizar usuarios preloaded que NO tienen datos de kills con valores por defecto
        List<UserEntity> allUsers = userRepository.findAll();
        for (UserEntity user : allUsers) {
            if (!allKillsUsers.contains(user.getName())) {
                // Usuario sin datos de kills - establecer valores por defecto realistas
                userService.updateUserStatsWithMatches(user.getName(), 0, 0, 5.0, 0);
                System.out.println("Usuario sin datos de kills - establecidos valores por defecto para: " + user.getName());
            }
        }
        
        System.out.println("Estadísticas actualizadas para " + allKillsUsers.size() + " usuarios con datos reales");
    }
    
    /**
     * Actualiza las estadísticas de un usuario específico basándose en los datos reales de kills
     */
    private void updateUserStatsFromKills(String userName) {
        try {
            // Obtener estadísticas reales de kills
            Long totalKillsFromKills = killRepository.countKillsByUser(userName);
            Long totalDeathsFromKills = killRepository.countDeathsByUser(userName);
            
            int actualKills = (totalKillsFromKills != null) ? totalKillsFromKills.intValue() : 0;
            int actualDeaths = (totalDeathsFromKills != null) ? totalDeathsFromKills.intValue() : 0;
            
            // Calcular cuántas partidas diferentes ha jugado este usuario
            int totalMatches = calculateUserMatches(userName);
            
            // Calcular estadísticas usando los métodos del sistema
            int goodPlays = calculateGoodPlays(actualKills);
            int badPlays = calculateBadPlays(actualKills);
            double score = calculateUnifiedScore(actualKills, actualDeaths, goodPlays, badPlays);
            
            // Actualizar el usuario en la base de datos con matches reales
            userService.updateUserStatsWithMatches(userName, actualKills, actualDeaths, score, totalMatches);
            
            System.out.println("Actualizadas estadísticas para " + userName + 
                              ": " + actualKills + " kills, " + actualDeaths + " deaths, " + totalMatches + " matches, score: " + String.format("%.2f", score));
                              
        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas para " + userName + ": " + e.getMessage());
        }
    }
    
    /**
     * Calcula cuántas partidas diferentes ha jugado un usuario
     */
    private int calculateUserMatches(String userName) {
        try {
            // Obtener todos los matchIds únicos donde aparece este usuario
            List<String> matchIds = killRepository.findDistinctMatchIdsByUser(userName);
            int matches = matchIds != null ? matchIds.size() : 0;
            System.out.println("Usuario " + userName + " aparece en " + matches + " matches: " + matchIds);
            return matches;
        } catch (Exception e) {
            System.err.println("Error calculando matches para " + userName + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    // Métodos de cálculo reutilizados del DatabaseMatchService
    private int calculateGoodPlays(int kills) {
        // Deterministic calculation: good plays are 30-50% of kills
        // Use kills as seed for consistent results
        double seed = kills * 0.12345; // Deterministic seed
        double factor = 0.3 + (seed % 0.2); // 30-50% range
        return (int)(kills * factor);
    }

    private int calculateBadPlays(int kills) {
        // Deterministic calculation: bad plays are 10-20% of kills
        // Use kills as seed for consistent results
        double seed = kills * 0.67890; // Different seed for variety
        double factor = 0.1 + (seed % 0.1); // 10-20% range
        return (int)(kills * factor);
    }

    private double calculateUnifiedScore(int kills, int deaths, int goodPlays, int badPlays) {
        // Fórmula unificada que incluye KDR y jugadas buenas/malas
        // Ponderación: 2/3 jugadas, 1/3 KDR

        // Calcular KDR
        double kdr = deaths > 0 ? (double) kills / deaths : kills;

        // Componente KDR (1/3 del peso total)
        double kdrComponent = Math.min(kdr * 1.5, 6.0); // Max 6 puntos por KDR
        kdrComponent = Math.max(kdrComponent, 0.0); // Mínimo 0

        // Componente de jugadas (2/3 del peso total)
        double playsComponent = 0.0;
        if (kills > 0) {
            // Ratio de buenas jugadas vs total de actividad
            double goodPlayRatio = (double) goodPlays / kills;
            double badPlayRatio = (double) badPlays / kills;

            // Score basado en la diferencia entre buenas y malas jugadas
            double playDifference = goodPlayRatio - badPlayRatio;
            playsComponent = Math.min(playDifference * 8.0, 8.0); // Max 8 puntos
            playsComponent = Math.max(playsComponent, -2.0); // Mínimo -2 puntos
        }

        // Combinar componentes con ponderación
        double rawScore = (playsComponent * 2.0/3.0) + (kdrComponent * 1.0/3.0);

        // Ajustar al rango 1.0-10.0
        double adjustedScore = rawScore + 5.0; // Centrar en 5.0
        return Math.min(Math.max(adjustedScore, 1.0), 10.0);
    }
}
