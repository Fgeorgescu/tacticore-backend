package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.MapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapRepository extends JpaRepository<MapEntity, Long> {
    
    // Buscar mapas activos
    List<MapEntity> findByIsActiveTrue();
    
    // Buscar mapa por nombre
    MapEntity findByName(String name);
    
    // Verificar si existe un mapa por nombre
    boolean existsByName(String name);
    
    // Obtener todos los nombres de mapas activos
    @org.springframework.data.jpa.repository.Query("SELECT m.name FROM MapEntity m WHERE m.isActive = true ORDER BY m.name")
    List<String> findAllActiveMapNames();
}
