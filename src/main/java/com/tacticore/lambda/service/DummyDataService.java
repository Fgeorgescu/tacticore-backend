package com.tacticore.lambda.service;

import com.tacticore.lambda.model.AnalyticsDataEntity;
import com.tacticore.lambda.model.MapEntity;
import com.tacticore.lambda.model.WeaponEntity;
import com.tacticore.lambda.repository.AnalyticsDataRepository;
import com.tacticore.lambda.repository.MapRepository;
import com.tacticore.lambda.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DummyDataService {
    
    @Autowired
    private MapRepository mapRepository;
    
    @Autowired
    private WeaponRepository weaponRepository;
    
    @Autowired
    private AnalyticsDataRepository analyticsDataRepository;
    
    public void loadDummyData() {
        loadDummyMaps();
        loadDummyWeapons();
        loadDummyAnalytics();
    }
    
    private void loadDummyMaps() {
        if (mapRepository.count() == 0) {
            List<MapEntity> maps = Arrays.asList(
                new MapEntity("Dust2", "Classic desert map with long corridors"),
                new MapEntity("Mirage", "Middle Eastern themed map"),
                new MapEntity("Inferno", "Italian themed map with tight corners"),
                new MapEntity("Cache", "Industrial themed map"),
                new MapEntity("Overpass", "German themed map with multiple levels"),
                new MapEntity("Nuke", "Nuclear power plant themed map"),
                new MapEntity("Train", "Train yard themed map"),
                new MapEntity("Cobblestone", "Medieval castle themed map"),
                new MapEntity("Ancient", "Ancient temple themed map"),
                new MapEntity("Anubis", "Egyptian themed map")
            );
            
            mapRepository.saveAll(maps);
            System.out.println("Datos dummy de mapas cargados: " + maps.size() + " mapas");
        }
    }
    
    private void loadDummyWeapons() {
        if (weaponRepository.count() == 0) {
            List<WeaponEntity> weapons = Arrays.asList(
                // Rifles
                new WeaponEntity("AK-47", "rifle", 36, 2700),
                new WeaponEntity("M4A4", "rifle", 33, 3100),
                new WeaponEntity("M4A1-S", "rifle", 33, 3100),
                new WeaponEntity("Galil AR", "rifle", 30, 2000),
                new WeaponEntity("FAMAS", "rifle", 30, 2250),
                new WeaponEntity("SG 553", "rifle", 30, 3000),
                new WeaponEntity("AUG", "rifle", 28, 3300),
                
                // Pistols
                new WeaponEntity("Glock-18", "pistol", 28, 200),
                new WeaponEntity("USP-S", "pistol", 35, 200),
                new WeaponEntity("P250", "pistol", 38, 300),
                new WeaponEntity("Desert Eagle", "pistol", 63, 700),
                new WeaponEntity("Five-SeveN", "pistol", 26, 500),
                new WeaponEntity("CZ75-Auto", "pistol", 31, 500),
                new WeaponEntity("P2000", "pistol", 35, 200),
                
                // Snipers
                new WeaponEntity("AWP", "sniper", 115, 4750),
                new WeaponEntity("SSG 08", "sniper", 88, 1700),
                new WeaponEntity("SCAR-20", "sniper", 80, 5000),
                new WeaponEntity("G3SG1", "sniper", 80, 5000),
                
                // SMGs
                new WeaponEntity("MAC-10", "smg", 29, 1050),
                new WeaponEntity("MP9", "smg", 26, 1250),
                new WeaponEntity("MP7", "smg", 29, 1500),
                new WeaponEntity("UMP-45", "smg", 35, 1200),
                new WeaponEntity("P90", "smg", 26, 2350),
                new WeaponEntity("PP-Bizon", "smg", 27, 1400),
                
                // Shotguns
                new WeaponEntity("Nova", "shotgun", 26, 1200),
                new WeaponEntity("XM1014", "shotgun", 20, 2000),
                new WeaponEntity("MAG-7", "shotgun", 71, 1300),
                new WeaponEntity("Sawed-Off", "shotgun", 32, 1200),
                
                // Machine Guns
                new WeaponEntity("M249", "machinegun", 32, 5200),
                new WeaponEntity("Negev", "machinegun", 35, 1700)
            );
            
            weaponRepository.saveAll(weapons);
            System.out.println("Datos dummy de armas cargados: " + weapons.size() + " armas");
        }
    }
    
    private void loadDummyAnalytics() {
        if (analyticsDataRepository.count() == 0) {
            List<AnalyticsDataEntity> analytics = Arrays.asList(
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 1), 18, 22, 0.82, 6.5, 4, 8, 1),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 3), 21, 19, 1.11, 7.2, 6, 6, 2),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 5), 24, 18, 1.33, 8.1, 8, 4, 3),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 7), 19, 25, 0.76, 5.8, 3, 9, 4),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 9), 27, 16, 1.69, 8.7, 11, 3, 5),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 11), 22, 20, 1.1, 7.5, 7, 5, 6),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 13), 31, 12, 2.58, 9.1, 12, 2, 7),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 15), 24, 18, 1.33, 8.5, 8, 3, 8),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 17), 28, 15, 1.87, 8.8, 10, 2, 9),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 19), 26, 17, 1.53, 8.2, 9, 4, 10),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 21), 23, 19, 1.21, 7.8, 7, 5, 11),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 23), 30, 14, 2.14, 9.0, 11, 3, 12),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 25), 25, 16, 1.56, 8.3, 8, 4, 13),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 27), 29, 13, 2.23, 8.9, 10, 2, 14),
                new AnalyticsDataEntity(LocalDate.of(2024, 1, 29), 27, 15, 1.80, 8.6, 9, 3, 15)
            );
            
            analyticsDataRepository.saveAll(analytics);
            System.out.println("Datos dummy de analytics cargados: " + analytics.size() + " registros");
        }
    }
    
    public void clearDummyData() {
        analyticsDataRepository.deleteAll();
        weaponRepository.deleteAll();
        mapRepository.deleteAll();
        System.out.println("Datos dummy eliminados");
    }
}
