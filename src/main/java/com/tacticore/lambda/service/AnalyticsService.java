package com.tacticore.lambda.service;

import com.tacticore.lambda.model.AnalyticsData;
import com.tacticore.lambda.model.AnalyticsDataEntity;
import com.tacticore.lambda.model.DashboardStats;
import com.tacticore.lambda.repository.AnalyticsDataRepository;
import com.tacticore.lambda.repository.KillRepository;
import com.tacticore.lambda.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private AnalyticsDataRepository analyticsDataRepository;
    
    @Autowired
    private KillRepository killRepository;
    
    @Autowired
    private MatchRepository matchRepository;
    
    // Obtener datos históricos de analytics
    public List<AnalyticsData> getHistoricalAnalytics(String timeRange, String metric) {
        List<AnalyticsDataEntity> entities;
        
        // Determinar el rango de fechas basado en timeRange
        LocalDate startDate = switch (timeRange.toLowerCase()) {
            case "week" -> LocalDate.now().minusWeeks(1);
            case "month" -> LocalDate.now().minusMonths(1);
            case "year" -> LocalDate.now().minusYears(1);
            default -> LocalDate.of(2020, 1, 1); // "all"
        };
        
        if ("all".equals(timeRange.toLowerCase())) {
            entities = analyticsDataRepository.findAllByOrderByDateAsc();
        } else {
            entities = analyticsDataRepository.findByDateBetween(startDate, LocalDate.now());
        }
        
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Obtener estadísticas del dashboard
    public DashboardStats getDashboardStats() {
        // Calcular estadísticas desde los datos reales de kills y matches
        Long totalKills = killRepository.count();
        Long totalMatches = matchRepository.count();
        
        // Calcular kills y deaths por usuario para obtener KDR promedio
        List<String> users = killRepository.findAllAttackers();
        double totalKdr = 0.0;
        int userCount = 0;
        
        for (String user : users) {
            Long userKills = killRepository.countKillsByUser(user);
            Long userDeaths = killRepository.countDeathsByUser(user);
            if (userDeaths > 0) {
                totalKdr += (double) userKills / userDeaths;
                userCount++;
            }
        }
        
        double averageKdr = userCount > 0 ? totalKdr / userCount : 0.0;
        
        // Calcular estadísticas adicionales
        Long totalDeaths = killRepository.countDeathsByUser(""); // Esto necesita ser ajustado
        Long totalHeadshots = killRepository.countHeadshotsByUser(""); // Esto también
        
        // Por ahora, usar valores calculados básicos
        int totalGoodPlays = (int) (totalKills * 0.3); // Estimación: 30% de kills son "good plays"
        int totalBadPlays = (int) (totalKills * 0.1); // Estimación: 10% de kills son "bad plays"
        double averageScore = totalKills > 0 ? (double) totalKills / totalMatches : 0.0;
        
        return new DashboardStats(
            totalMatches.intValue(),
            totalKills.intValue(),
            totalDeaths.intValue(),
            totalGoodPlays,
            totalBadPlays,
            averageScore,
            averageKdr
        );
    }
    
    // Convertir entidad a DTO
    private AnalyticsData convertToDto(AnalyticsDataEntity entity) {
        return new AnalyticsData(
            entity.getDate(),
            entity.getKills(),
            entity.getDeaths(),
            entity.getKdr(),
            entity.getScore(),
            entity.getGoodPlays(),
            entity.getBadPlays(),
            entity.getMatches()
        );
    }
    
    // Agregar nuevo dato de analytics
    public AnalyticsDataEntity addAnalyticsData(AnalyticsData analyticsData) {
        AnalyticsDataEntity entity = new AnalyticsDataEntity(
            analyticsData.getDate(),
            analyticsData.getKills(),
            analyticsData.getDeaths(),
            analyticsData.getKdr(),
            analyticsData.getScore(),
            analyticsData.getGoodPlays(),
            analyticsData.getBadPlays(),
            analyticsData.getMatches()
        );
        return analyticsDataRepository.save(entity);
    }
}
