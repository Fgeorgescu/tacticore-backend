package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.WeaponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeaponRepository extends JpaRepository<WeaponEntity, Long> {
    
    // Buscar armas activas
    List<WeaponEntity> findByIsActiveTrue();
    
    // Buscar armas por categoría
    List<WeaponEntity> findByCategoryAndIsActiveTrue(String category);
    
    // Buscar arma por nombre
    WeaponEntity findByName(String name);
    
    // Verificar si existe un arma por nombre
    boolean existsByName(String name);
    
    // Obtener todas las categorías únicas
    @Query("SELECT DISTINCT w.category FROM WeaponEntity w WHERE w.isActive = true ORDER BY w.category")
    List<String> findAllActiveCategories();
    
    // Obtener todos los nombres de armas activas
    @Query("SELECT w.name FROM WeaponEntity w WHERE w.isActive = true ORDER BY w.name")
    List<String> findAllActiveWeaponNames();
    
    // Obtener armas ordenadas por precio
    List<WeaponEntity> findByIsActiveTrueOrderByPriceAsc();
}
