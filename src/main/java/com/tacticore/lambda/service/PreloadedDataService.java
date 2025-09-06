package com.tacticore.lambda.service;

import com.tacticore.lambda.model.ChatMessageEntity;
import com.tacticore.lambda.model.MatchEntity;
import com.tacticore.lambda.repository.ChatMessageRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PreloadedDataService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
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
    
    public void clearPreloadedData() {
        chatMessageRepository.deleteAll();
        matchRepository.deleteAll();
        System.out.println("Datos precargados eliminados");
    }
}
