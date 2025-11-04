package com.tacticore.lambda.service;

import com.tacticore.lambda.model.ChatMessageEntity;
import com.tacticore.lambda.model.dto.ChatMessageDto;
import com.tacticore.lambda.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    void testGetChatMessages() {
        // Given
        String matchId = "match123";
        ChatMessageEntity entity1 = new ChatMessageEntity(matchId, "user1", "Hello");
        entity1.setId(1L);
        entity1.setCreatedAt(LocalDateTime.now());
        
        ChatMessageEntity entity2 = new ChatMessageEntity(matchId, "user2", "Hi there");
        entity2.setId(2L);
        entity2.setCreatedAt(LocalDateTime.now().plusMinutes(1));
        
        when(chatMessageRepository.findByMatchIdOrderByCreatedAtAsc(matchId))
            .thenReturn(Arrays.asList(entity1, entity2));

        // When
        List<ChatMessageDto> result = chatService.getChatMessages(matchId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUser());
        assertEquals("Hello", result.get(0).getMessage());
        assertEquals("user2", result.get(1).getUser());
        assertEquals("Hi there", result.get(1).getMessage());
        verify(chatMessageRepository).findByMatchIdOrderByCreatedAtAsc(matchId);
    }

    @Test
    void testGetChatMessages_Empty() {
        // Given
        String matchId = "match123";
        when(chatMessageRepository.findByMatchIdOrderByCreatedAtAsc(matchId))
            .thenReturn(Arrays.asList());

        // When
        List<ChatMessageDto> result = chatService.getChatMessages(matchId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chatMessageRepository).findByMatchIdOrderByCreatedAtAsc(matchId);
    }

    @Test
    void testAddChatMessage() {
        // Given
        String matchId = "match123";
        String userName = "testUser";
        String message = "Test message";
        
        ChatMessageEntity savedEntity = new ChatMessageEntity(matchId, userName, message);
        savedEntity.setId(1L);
        savedEntity.setCreatedAt(LocalDateTime.now());
        
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(savedEntity);

        // When
        ChatMessageDto result = chatService.addChatMessage(matchId, userName, message);

        // Then
        assertNotNull(result);
        assertEquals(userName, result.getUser());
        assertEquals(message, result.getMessage());
        assertNotNull(result.getTimestamp());
        verify(chatMessageRepository).save(any(ChatMessageEntity.class));
    }

    @Test
    void testAddChatMessage_VerifyEntityCreation() {
        // Given
        String matchId = "match123";
        String userName = "testUser";
        String message = "Test message";
        
        ChatMessageEntity savedEntity = new ChatMessageEntity(matchId, userName, message);
        savedEntity.setId(1L);
        savedEntity.setCreatedAt(LocalDateTime.now());
        
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenReturn(savedEntity);

        // When
        chatService.addChatMessage(matchId, userName, message);

        // Then
        verify(chatMessageRepository).save(argThat(entity -> 
            entity.getMatchId().equals(matchId) &&
            entity.getUserName().equals(userName) &&
            entity.getMessage().equals(message)
        ));
    }

    @Test
    void testGetChatMessages_OrderedByCreatedAt() {
        // Given
        String matchId = "match123";
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 1, 10, 1);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 1, 10, 2);
        
        ChatMessageEntity entity1 = new ChatMessageEntity(matchId, "user1", "First");
        entity1.setId(1L);
        entity1.setCreatedAt(time1);
        
        ChatMessageEntity entity2 = new ChatMessageEntity(matchId, "user2", "Second");
        entity2.setId(2L);
        entity2.setCreatedAt(time2);
        
        ChatMessageEntity entity3 = new ChatMessageEntity(matchId, "user3", "Third");
        entity3.setId(3L);
        entity3.setCreatedAt(time3);
        
        when(chatMessageRepository.findByMatchIdOrderByCreatedAtAsc(matchId))
            .thenReturn(Arrays.asList(entity1, entity2, entity3));

        // When
        List<ChatMessageDto> result = chatService.getChatMessages(matchId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("First", result.get(0).getMessage());
        assertEquals("Second", result.get(1).getMessage());
        assertEquals("Third", result.get(2).getMessage());
        assertEquals(time1, result.get(0).getTimestamp());
        assertEquals(time2, result.get(1).getTimestamp());
        assertEquals(time3, result.get(2).getTimestamp());
    }
}

