package com.tacticore.lambda.controller;

import com.tacticore.lambda.service.DataLoaderService;
import com.tacticore.lambda.service.DummyDataService;
import com.tacticore.lambda.service.PreloadedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {
    
    @Autowired
    private DataLoaderService dataLoaderService;
    
    @Autowired
    private PreloadedDataService preloadedDataService;
    
    @Autowired
    private DummyDataService dummyDataService;
    
    // POST /api/data/load
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadData(@RequestParam(defaultValue = "example.json") String fileName) {
        try {
            dataLoaderService.loadDataFromJson(fileName);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Datos cargados exitosamente desde " + fileName
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error cargando datos: " + e.getMessage()
            ));
        }
    }
    
    // DELETE /api/data/clear
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearData() {
        dataLoaderService.clearAllData();
        preloadedDataService.clearPreloadedData();
        dummyDataService.clearDummyData();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Todos los datos han sido eliminados"
        ));
    }
    
    // POST /api/data/reload-preloaded
    @PostMapping("/reload-preloaded")
    public ResponseEntity<Map<String, String>> reloadPreloadedData() {
        try {
            preloadedDataService.loadPreloadedData();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Datos precargados recargados exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error recargando datos precargados: " + e.getMessage()
            ));
        }
    }
    
    // POST /api/data/reload-dummy
    @PostMapping("/reload-dummy")
    public ResponseEntity<Map<String, String>> reloadDummyData() {
        try {
            dummyDataService.loadDummyData();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Datos dummy recargados exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error recargando datos dummy: " + e.getMessage()
            ));
        }
    }
    
    // GET /api/data/status
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getDataStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "ready",
            "message", "Base de datos en memoria lista para usar"
        ));
    }
}
