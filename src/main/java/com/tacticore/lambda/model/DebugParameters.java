package com.tacticore.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Parámetros de debug para el procesamiento de coordenadas
 */
public class DebugParameters {
    
    @JsonProperty("pos_x")
    private Double posX;
    
    @JsonProperty("pos_y")
    private Double posY;
    
    @JsonProperty("scale")
    private Double scale;
    
    @JsonProperty("x_adjust")
    private Integer xAdjust;
    
    @JsonProperty("y_adjust")
    private Integer yAdjust;
    
    // Constructor por defecto
    public DebugParameters() {}
    
    public DebugParameters(Double posX, Double posY, Double scale, Integer xAdjust, Integer yAdjust) {
        this.posX = posX;
        this.posY = posY;
        this.scale = scale;
        this.xAdjust = xAdjust;
        this.yAdjust = yAdjust;
    }
    
    // Getters y Setters
    public Double getPosX() {
        return posX;
    }
    
    public void setPosX(Double posX) {
        this.posX = posX;
    }
    
    public Double getPosY() {
        return posY;
    }
    
    public void setPosY(Double posY) {
        this.posY = posY;
    }
    
    public Double getScale() {
        return scale;
    }
    
    public void setScale(Double scale) {
        this.scale = scale;
    }
    
    public Integer getXAdjust() {
        return xAdjust;
    }
    
    public void setXAdjust(Integer xAdjust) {
        this.xAdjust = xAdjust;
    }
    
    public Integer getYAdjust() {
        return yAdjust;
    }
    
    public void setYAdjust(Integer yAdjust) {
        this.yAdjust = yAdjust;
    }
    
    @Override
    public String toString() {
        return "DebugParameters{" +
                "posX=" + posX +
                ", posY=" + posY +
                ", scale=" + scale +
                ", xAdjust=" + xAdjust +
                ", yAdjust=" + yAdjust +
                '}';
    }
}
