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
            ),
            new MatchEntity(
                "3", 
                "inferno_training_2024.dem", 
                "Inferno", 
                64, 
                31, 
                "processed", 
                true
            ),
            new MatchEntity(
                "4", 
                "cache_ranked_2024.dem", 
                "Cache", 
                64, 
                28, 
                "processed", 
                true
            ),
            new MatchEntity(
                "5", 
                "overpass_casual_2024.dem", 
                "Overpass", 
                64, 
                22, 
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
            // Mensajes para match 1
            new ChatMessageEntity(
                "1", 
                "Analyst", 
                "¿Qué opinas de la jugada en el round 3?", 
                LocalDateTime.of(2024, 1, 15, 14, 30)
            ),
            new ChatMessageEntity(
                "1", 
                "Player", 
                "Fue una buena rotación, aproveché que estaban distraídos en B", 
                LocalDateTime.of(2024, 1, 15, 14, 32)
            ),
            new ChatMessageEntity(
                "1", 
                "Coach", 
                "El timing fue perfecto, pero podrías haber usado mejor cobertura", 
                LocalDateTime.of(2024, 1, 15, 14, 35)
            ),
            new ChatMessageEntity(
                "1", 
                "Analyst", 
                "El headshot en el round 5 fue impresionante", 
                LocalDateTime.of(2024, 1, 15, 14, 40)
            ),
            
            // Mensajes para match 2
            new ChatMessageEntity(
                "2", 
                "Player", 
                "Esta partida casual está muy equilibrada", 
                LocalDateTime.of(2024, 1, 14, 16, 20)
            ),
            new ChatMessageEntity(
                "2", 
                "Spectator", 
                "Buena estrategia en Mirage, especialmente en el sitio A", 
                LocalDateTime.of(2024, 1, 14, 16, 25)
            ),
            new ChatMessageEntity(
                "2", 
                "Coach", 
                "Necesitas mejorar el crosshair placement", 
                LocalDateTime.of(2024, 1, 14, 16, 30)
            ),
            
            // Mensajes para match 3
            new ChatMessageEntity(
                "3", 
                "Analyst", 
                "Excelente entrenamiento en Inferno", 
                LocalDateTime.of(2024, 1, 13, 10, 15)
            ),
            new ChatMessageEntity(
                "3", 
                "Player", 
                "Los smokes están funcionando muy bien", 
                LocalDateTime.of(2024, 1, 13, 10, 18)
            ),
            new ChatMessageEntity(
                "3", 
                "Coach", 
                "El trabajo en equipo mejoró mucho", 
                LocalDateTime.of(2024, 1, 13, 10, 22)
            ),
            
            // Mensajes para match 4
            new ChatMessageEntity(
                "4", 
                "Player", 
                "Cache es uno de mis mapas favoritos", 
                LocalDateTime.of(2024, 1, 12, 20, 10)
            ),
            new ChatMessageEntity(
                "4", 
                "Analyst", 
                "Buena estrategia en el sitio B", 
                LocalDateTime.of(2024, 1, 12, 20, 15)
            ),
            
            // Mensajes para match 5
            new ChatMessageEntity(
                "5", 
                "Spectator", 
                "Overpass tiene muchas oportunidades de flanqueo", 
                LocalDateTime.of(2024, 1, 11, 18, 30)
            ),
            new ChatMessageEntity(
                "5", 
                "Player", 
                "El timing de las rotaciones fue clave", 
                LocalDateTime.of(2024, 1, 11, 18, 35)
            )
        );
        
        chatMessageRepository.saveAll(preloadedMessages);
        System.out.println("Mensajes de chat precargados: " + preloadedMessages.size());
    }
    
    public void clearPreloadedData() {
        chatMessageRepository.deleteAll();
        matchRepository.deleteAll();
        System.out.println("Datos precargados eliminados");
    }
}
