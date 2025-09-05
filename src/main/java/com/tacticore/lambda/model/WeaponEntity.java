package com.tacticore.lambda.model;

import jakarta.persistence.*;

@Entity
@Table(name = "weapons")
public class WeaponEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "category")
    private String category; // rifle, pistol, sniper, etc.
    
    @Column(name = "damage")
    private Integer damage;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    // Constructors
    public WeaponEntity() {
        this.createdAt = java.time.LocalDateTime.now();
    }
    
    public WeaponEntity(String name, String category) {
        this();
        this.name = name;
        this.category = category;
    }
    
    public WeaponEntity(String name, String category, Integer damage, Integer price) {
        this();
        this.name = name;
        this.category = category;
        this.damage = damage;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getDamage() { return damage; }
    public void setDamage(Integer damage) { this.damage = damage; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
