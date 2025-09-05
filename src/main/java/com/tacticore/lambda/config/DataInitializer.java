package com.tacticore.lambda.config;

import com.tacticore.lambda.service.DataLoaderService;
import com.tacticore.lambda.service.DummyDataService;
import com.tacticore.lambda.service.PreloadedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private DataLoaderService dataLoaderService;
    
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
            
            System.out.println("Inicializando datos precargados...");
            preloadedDataService.loadPreloadedData();
            System.out.println("Datos precargados inicializados exitosamente!");
            
            System.out.println("Inicializando datos desde example.json...");
            dataLoaderService.loadDataFromJson("example.json");
            System.out.println("Datos de kills inicializados exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error inicializando datos: " + e.getMessage());
            System.err.println("La aplicación continuará sin datos iniciales.");
        }
    }
}
