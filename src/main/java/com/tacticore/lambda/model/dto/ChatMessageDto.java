package com.tacticore.lambda.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ChatMessageDto {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("user")
    private String user;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    // Constructors
    public ChatMessageDto() {}
    
    public ChatMessageDto(int id, String user, String message, LocalDateTime timestamp) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
