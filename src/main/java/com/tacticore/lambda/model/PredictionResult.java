package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Resultado de la predicción del modelo de IA
 */
public class PredictionResult {
    
    @JsonProperty("predicted_label")
    private String predictedLabel;
    
    @JsonProperty("confidence")
    private Double confidence;
    
    @JsonProperty("top_predictions")
    private List<TopPrediction> topPredictions;
    
    @JsonProperty("all_probabilities")
    private Map<String, Double> allProbabilities;
    
    // Constructor por defecto
    public PredictionResult() {}
    
    public PredictionResult(String predictedLabel, Double confidence, List<TopPrediction> topPredictions,
                          Map<String, Double> allProbabilities) {
        this.predictedLabel = predictedLabel;
        this.confidence = confidence;
        this.topPredictions = topPredictions;
        this.allProbabilities = allProbabilities;
    }
    
    // Getters y Setters
    public String getPredictedLabel() {
        return predictedLabel;
    }
    
    public void setPredictedLabel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public List<TopPrediction> getTopPredictions() {
        return topPredictions;
    }
    
    public void setTopPredictions(List<TopPrediction> topPredictions) {
        this.topPredictions = topPredictions;
    }
    
    public Map<String, Double> getAllProbabilities() {
        return allProbabilities;
    }
    
    public void setAllProbabilities(Map<String, Double> allProbabilities) {
        this.allProbabilities = allProbabilities;
    }
    
    @Override
    public String toString() {
        return "PredictionResult{" +
                "predictedLabel='" + predictedLabel + '\'' +
                ", confidence=" + confidence +
                ", topPredictions=" + topPredictions +
                ", allProbabilities=" + allProbabilities +
                '}';
    }
}
