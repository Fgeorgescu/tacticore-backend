package com.tacticore.lambda.service;

import com.tacticore.lambda.model.ChatMessageEntity;
import com.tacticore.lambda.model.dto.ChatMessageDto;
import com.tacticore.lambda.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    public List<ChatMessageDto> getChatMessages(String matchId) {
        List<ChatMessageEntity> entities = chatMessageRepository.findByMatchIdOrderByCreatedAtAsc(matchId);
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ChatMessageDto addChatMessage(String matchId, String userName, String message) {
        ChatMessageEntity entity = new ChatMessageEntity(matchId, userName, message);
        ChatMessageEntity savedEntity = chatMessageRepository.save(entity);
        return convertToDto(savedEntity);
    }
    
    private ChatMessageDto convertToDto(ChatMessageEntity entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId().intValue());
        dto.setUser(entity.getUserName());
        dto.setMessage(entity.getMessage());
        dto.setTimestamp(entity.getCreatedAt());
        return dto;
    }
}
