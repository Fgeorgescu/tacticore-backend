package com.tesis.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("message")
    private String message;
    
    // Constructors
    public MatchResponse() {}
    
    public MatchResponse(String id, String status, String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Static factory methods
    public static MatchResponse processing(String id) {
        return new MatchResponse(id, "processing", "Match uploaded successfully and is being processed");
    }
    
    public static MatchResponse completed(String id) {
        return new MatchResponse(id, "completed", "Match processing completed successfully");
    }
    
    public static MatchResponse failed(String id, String error) {
        return new MatchResponse(id, "failed", "Match processing failed: " + error);
    }
}
