package com.tacticore.lambda.config;

import com.tacticore.lambda.service.DummyDataService;
import com.tacticore.lambda.service.PreloadedDataService;
// import com.tacticore.lambda.service.PreloadedUserService; // Desactivado - se usa PreloadedDataService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private PreloadedDataService preloadedDataService;
    
    @Autowired
    private DummyDataService dummyDataService;
    
    // @Autowired
    // private PreloadedUserService preloadedUserService; // Desactivado - se usa PreloadedDataService
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // System.out.println("Inicializando usuarios precargados...");
            // preloadedUserService.loadPreloadedUsers(); // Desactivado - los usuarios se crean en PreloadedDataService
            // System.out.println("Usuarios precargados inicializados exitosamente!");
            
            System.out.println("Inicializando datos dummy (maps, weapons, analytics)...");
            dummyDataService.loadDummyData();
            System.out.println("Datos dummy inicializados exitosamente!");
            
            System.out.println("Inicializando todas las partidas demo (incluye usuarios con roles aleatorios)...");
            preloadedDataService.loadAllDemoMatches();
            System.out.println("Todas las partidas demo inicializadas exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error inicializando datos: " + e.getMessage());
            System.err.println("La aplicación continuará sin datos iniciales.");
        }
    }
}
