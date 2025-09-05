package com.tacticore.lambda.service;

import com.tacticore.lambda.model.MapEntity;
import com.tacticore.lambda.model.WeaponEntity;
import com.tacticore.lambda.repository.MapRepository;
import com.tacticore.lambda.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameDataService {
    
    @Autowired
    private MapRepository mapRepository;
    
    @Autowired
    private WeaponRepository weaponRepository;
    
    // === MAPS ===
    
    // Obtener todos los mapas activos
    public List<MapEntity> getAllActiveMaps() {
        return mapRepository.findByIsActiveTrue();
    }
    
    // Obtener solo los nombres de mapas activos (para compatibilidad con frontend)
    public List<String> getActiveMapNames() {
        return mapRepository.findAllActiveMapNames();
    }
    
    // Obtener mapa por nombre
    public MapEntity getMapByName(String name) {
        return mapRepository.findByName(name);
    }
    
    // Agregar nuevo mapa
    public MapEntity addMap(String name, String description) {
        MapEntity map = new MapEntity(name, description);
        return mapRepository.save(map);
    }
    
    // === WEAPONS ===
    
    // Obtener todas las armas activas
    public List<WeaponEntity> getAllActiveWeapons() {
        return weaponRepository.findByIsActiveTrue();
    }
    
    // Obtener solo los nombres de armas activas (para compatibilidad con frontend)
    public List<String> getActiveWeaponNames() {
        return weaponRepository.findAllActiveWeaponNames();
    }
    
    // Obtener armas por categoría
    public List<WeaponEntity> getWeaponsByCategory(String category) {
        return weaponRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    // Obtener todas las categorías de armas
    public List<String> getAllWeaponCategories() {
        return weaponRepository.findAllActiveCategories();
    }
    
    // Obtener arma por nombre
    public WeaponEntity getWeaponByName(String name) {
        return weaponRepository.findByName(name);
    }
    
    // Agregar nueva arma
    public WeaponEntity addWeapon(String name, String category, Integer damage, Integer price) {
        WeaponEntity weapon = new WeaponEntity(name, category, damage, price);
        return weaponRepository.save(weapon);
    }
}
