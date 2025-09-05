package com.tacticore.lambda.repository;

import com.tacticore.lambda.model.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
    
    Optional<MatchEntity> findByMatchId(String matchId);
    
    boolean existsByMatchId(String matchId);
}
