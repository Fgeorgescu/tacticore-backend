package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Predicción individual dentro del top de predicciones
 */
public class TopPrediction {
    
    @JsonProperty("label")
    private String label;
    
    @JsonProperty("confidence")
    private Double confidence;
    
    // Constructor por defecto
    public TopPrediction() {}
    
    public TopPrediction(String label, Double confidence) {
        this.label = label;
        this.confidence = confidence;
    }
    
    // Getters y Setters
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    @Override
    public String toString() {
        return "TopPrediction{" +
                "label='" + label + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
