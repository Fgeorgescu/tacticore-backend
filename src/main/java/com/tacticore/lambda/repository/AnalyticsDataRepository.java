package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.AnalyticsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsDataRepository extends JpaRepository<AnalyticsDataEntity, Long> {
    
    // Buscar datos por rango de fechas
    List<AnalyticsDataEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Buscar datos por fecha específica
    List<AnalyticsDataEntity> findByDate(LocalDate date);
    
    // Obtener datos ordenados por fecha
    List<AnalyticsDataEntity> findAllByOrderByDateAsc();
    
    // Obtener datos ordenados por fecha descendente
    List<AnalyticsDataEntity> findAllByOrderByDateDesc();
    
    // Obtener el último registro
    @Query("SELECT a FROM AnalyticsDataEntity a ORDER BY a.date DESC LIMIT 1")
    AnalyticsDataEntity findLatestRecord();
    
    // Obtener estadísticas agregadas
    @Query("SELECT SUM(a.kills), SUM(a.deaths), AVG(a.kdr), AVG(a.score), SUM(a.goodPlays), SUM(a.badPlays), SUM(a.matches) FROM AnalyticsDataEntity a")
    Object[] getAggregatedStats();
}
