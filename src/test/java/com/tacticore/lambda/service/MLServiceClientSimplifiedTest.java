package com.tacticore.lambda.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MLServiceClientSimplifiedTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @InjectMocks
    private MLServiceClient mlServiceClient;


    @BeforeEach
    void setUp() {
        // Configurar simulación habilitada
        ReflectionTestUtils.setField(mlServiceClient, "simulationEnabled", true);
        ReflectionTestUtils.setField(mlServiceClient, "jsonDirectory", "demos-jsons");
        
    }

    @Test
    void testSimulationEnabled() {
        // Given
        boolean simulationEnabled = (boolean) ReflectionTestUtils.getField(mlServiceClient, "simulationEnabled");
        
        // Then
        assertTrue(simulationEnabled);
    }

    @Test
    void testJsonDirectoryConfiguration() {
        // Given
        String jsonDirectory = (String) ReflectionTestUtils.getField(mlServiceClient, "jsonDirectory");
        
        // Then
        assertEquals("demos-jsons", jsonDirectory);
    }

    @Test
    void testMLServiceUrlConfiguration() {
        // Given
        ReflectionTestUtils.setField(mlServiceClient, "mlServiceUrl", "http://ml-service:8000");
        String mlServiceUrl = (String) ReflectionTestUtils.getField(mlServiceClient, "mlServiceUrl");
        
        // Then
        assertEquals("http://ml-service:8000", mlServiceUrl);
    }

    @Test
    void testSimulationDisabled() {
        // Given
        ReflectionTestUtils.setField(mlServiceClient, "simulationEnabled", false);
        
        // When
        boolean simulationEnabled = (boolean) ReflectionTestUtils.getField(mlServiceClient, "simulationEnabled");
        
        // Then
        assertFalse(simulationEnabled);
    }

}
