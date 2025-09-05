package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Información de debug para el procesamiento de coordenadas
 */
public class DebugInfo {
    
    @JsonProperty("original")
    private List<Double> original;
    
    @JsonProperty("step1_centered")
    private List<Double> step1Centered;
    
    @JsonProperty("step2_scaled")
    private List<Double> step2Scaled;
    
    @JsonProperty("step3_centered")
    private List<Double> step3Centered;
    
    @JsonProperty("step4_adjusted")
    private List<Double> step4Adjusted;
    
    @JsonProperty("final")
    private List<Double> finalCoords;
    
    @JsonProperty("parameters")
    private DebugParameters parameters;
    
    // Constructor por defecto
    public DebugInfo() {}
    
    public DebugInfo(List<Double> original, List<Double> step1Centered, List<Double> step2Scaled,
                    List<Double> step3Centered, List<Double> step4Adjusted, List<Double> finalCoords,
                    DebugParameters parameters) {
        this.original = original;
        this.step1Centered = step1Centered;
        this.step2Scaled = step2Scaled;
        this.step3Centered = step3Centered;
        this.step4Adjusted = step4Adjusted;
        this.finalCoords = finalCoords;
        this.parameters = parameters;
    }
    
    // Getters y Setters
    public List<Double> getOriginal() {
        return original;
    }
    
    public void setOriginal(List<Double> original) {
        this.original = original;
    }
    
    public List<Double> getStep1Centered() {
        return step1Centered;
    }
    
    public void setStep1Centered(List<Double> step1Centered) {
        this.step1Centered = step1Centered;
    }
    
    public List<Double> getStep2Scaled() {
        return step2Scaled;
    }
    
    public void setStep2Scaled(List<Double> step2Scaled) {
        this.step2Scaled = step2Scaled;
    }
    
    public List<Double> getStep3Centered() {
        return step3Centered;
    }
    
    public void setStep3Centered(List<Double> step3Centered) {
        this.step3Centered = step3Centered;
    }
    
    public List<Double> getStep4Adjusted() {
        return step4Adjusted;
    }
    
    public void setStep4Adjusted(List<Double> step4Adjusted) {
        this.step4Adjusted = step4Adjusted;
    }
    
    public List<Double> getFinalCoords() {
        return finalCoords;
    }
    
    public void setFinalCoords(List<Double> finalCoords) {
        this.finalCoords = finalCoords;
    }
    
    public DebugParameters getParameters() {
        return parameters;
    }
    
    public void setParameters(DebugParameters parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public String toString() {
        return "DebugInfo{" +
                "original=" + original +
                ", step1Centered=" + step1Centered +
                ", step2Scaled=" + step2Scaled +
                ", step3Centered=" + step3Centered +
                ", step4Adjusted=" + step4Adjusted +
                ", finalCoords=" + finalCoords +
                ", parameters=" + parameters +
                '}';
    }
}
