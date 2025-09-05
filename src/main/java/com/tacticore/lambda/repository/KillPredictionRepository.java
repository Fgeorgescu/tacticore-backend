package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.KillPredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KillPredictionRepository extends JpaRepository<KillPredictionEntity, Long> {
    
    List<KillPredictionEntity> findByKillId(String killId);
    
    List<KillPredictionEntity> findByLabel(String label);
    
    List<KillPredictionEntity> findByIsTopPrediction(Boolean isTopPrediction);
    
    @Query("SELECT kp FROM KillPredictionEntity kp WHERE kp.killId = :killId AND kp.isTopPrediction = true")
    List<KillPredictionEntity> findTopPredictionsByKillId(@Param("killId") String killId);
    
    @Query("SELECT kp.label, COUNT(kp) FROM KillPredictionEntity kp WHERE kp.isTopPrediction = true GROUP BY kp.label ORDER BY COUNT(kp) DESC")
    List<Object[]> getTopPredictionStats();
    
    @Query("SELECT kp.label, AVG(kp.confidence) FROM KillPredictionEntity kp WHERE kp.isTopPrediction = true GROUP BY kp.label")
    List<Object[]> getAverageConfidenceByLabel();
    
    @Query("SELECT COUNT(kp) FROM KillPredictionEntity kp WHERE kp.label = :label AND kp.isTopPrediction = true")
    Long countTopPredictionsByLabel(@Param("label") String label);
    
    @Query("SELECT AVG(kp.confidence) FROM KillPredictionEntity kp WHERE kp.label = :label AND kp.isTopPrediction = true")
    Double getAverageConfidenceByLabel(@Param("label") String label);
}
