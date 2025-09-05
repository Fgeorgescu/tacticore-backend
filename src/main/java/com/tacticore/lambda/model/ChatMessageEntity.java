package com.tacticore.lambda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_id", nullable = false)
    private String matchId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public ChatMessageEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessageEntity(String matchId, String userName, String message) {
        this.matchId = matchId;
        this.userName = userName;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessageEntity(String matchId, String userName, String message, LocalDateTime createdAt) {
        this.matchId = matchId;
        this.userName = userName;
        this.message = message;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
