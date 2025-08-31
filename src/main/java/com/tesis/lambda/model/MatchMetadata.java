package com.tesis.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MatchMetadata {
    
    @NotNull(message = "playerName is required")
    @NotBlank(message = "playerName cannot be blank")
    @JsonProperty("playerName")
    private String playerName;
    
    @JsonProperty("notes")
    private String notes;
    
    // Constructors
    public MatchMetadata() {}
    
    public MatchMetadata(String playerName, String notes) {
        this.playerName = playerName;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
