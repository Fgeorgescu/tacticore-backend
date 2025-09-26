package com.tacticore.lambda.config;

import com.tacticore.lambda.service.DummyDataService;
import com.tacticore.lambda.service.PreloadedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private PreloadedDataService preloadedDataService;
    
    @Autowired
    private DummyDataService dummyDataService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("Inicializando datos dummy (maps, weapons, analytics)...");
            dummyDataService.loadDummyData();
            System.out.println("Datos dummy inicializados exitosamente!");
            
            System.out.println("Inicializando partida de Inferno...");
            preloadedDataService.loadInfernoMatch();
            System.out.println("Partida de Inferno inicializada exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error inicializando datos: " + e.getMessage());
            System.err.println("La aplicación continuará sin datos iniciales.");
        }
    }
}
