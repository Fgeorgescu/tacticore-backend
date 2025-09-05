package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    
    List<ChatMessageEntity> findByMatchIdOrderByCreatedAtAsc(String matchId);
    
    List<ChatMessageEntity> findByMatchId(String matchId);
}
