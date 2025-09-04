package com.tacticore.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ConfigurableApplicationContext applicationContext;

    static {
        // Inicializar Spring Boot solo una vez
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(LambdaApplication.class);
        }
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        System.out.println("Received request: " + input.getHttpMethod() + " " + input.getPath());
        
        try {
            // Crear respuesta base
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            
            // Headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
            response.setHeaders(headers);
            
            // Manejar OPTIONS para CORS
            if (HttpMethod.OPTIONS.name().equals(input.getHttpMethod())) {
                response.setStatusCode(200);
                return response;
            }
            
            // Obtener el contexto de Spring
            WebApplicationContext webContext = null;
            if (applicationContext instanceof WebApplicationContext) {
                webContext = (WebApplicationContext) applicationContext;
            }
            
            // Manejar diferentes rutas
            String path = input.getPath();
            String method = input.getHttpMethod();
            
            if ("/api/health".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleHealthCheck(response);
            } else if ("/api/matches".equals(path) && HttpMethod.POST.name().equals(method)) {
                return handleMatchUpload(input, response, webContext);
            } else if (path.matches("/api/matches/[^/]+") && HttpMethod.GET.name().equals(method)) {
                return handleMatchStatus(input, response, webContext);
            } else if ("/hello".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleHelloWorld(response);
            } else {
                return handleNotFound(response);
            }
            
        } catch (Exception e) {
            System.err.println("Error handling request: " + e.getMessage());
            return createErrorResponse("Internal server error: " + e.getMessage());
        }
    }
    
    private APIGatewayProxyResponseEvent handleHealthCheck(APIGatewayProxyResponseEvent response) {
        response.setStatusCode(200);
        response.setBody("{\"status\": \"healthy\", \"message\": \"Tacti-Core Backend is running!\"}");
        return response;
    }
    
    private APIGatewayProxyResponseEvent handleHelloWorld(APIGatewayProxyResponseEvent response) {
        System.out.println("Hello World from AWS Lambda!");
        
        response.setStatusCode(200);
        String body = "{\"message\": \"Hello World from AWS Lambda!\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";
        response.setBody(body);
        
        return response;
    }
    
    private APIGatewayProxyResponseEvent handleMatchUpload(APIGatewayProxyRequestEvent input, 
                                                         APIGatewayProxyResponseEvent response, 
                                                         WebApplicationContext webContext) {
        // Por ahora, simulamos la respuesta para el upload de matches
        // En un entorno real, aquí se procesaría el multipart form data
        
        response.setStatusCode(200);
        String matchId = "match_" + System.currentTimeMillis();
        String body = String.format("{\"id\": \"%s\", \"status\": \"processing\", \"message\": \"Match uploaded successfully and is being processed\"}", matchId);
        response.setBody(body);
        
        System.out.println("Simulated match upload for ID: " + matchId);
        return response;
    }
    
    private APIGatewayProxyResponseEvent handleMatchStatus(APIGatewayProxyRequestEvent input, 
                                                          APIGatewayProxyResponseEvent response, 
                                                          WebApplicationContext webContext) {
        String path = input.getPath();
        String matchId = path.substring(path.lastIndexOf('/') + 1);
        
        // Simular diferentes estados basados en el ID
        String status = matchId.contains("completed") ? "completed" : 
                       matchId.contains("failed") ? "failed" : "processing";
        
        String message = switch (status) {
            case "completed" -> "Match processing completed successfully";
            case "failed" -> "Match processing failed";
            default -> "Match is being processed";
        };
        
        response.setStatusCode(200);
        String body = String.format("{\"id\": \"%s\", \"status\": \"%s\", \"message\": \"%s\"}", matchId, status, message);
        response.setBody(body);
        
        System.out.println("Getting status for match: " + matchId + " - Status: " + status);
        return response;
    }
    
    private APIGatewayProxyResponseEvent handleNotFound(APIGatewayProxyResponseEvent response) {
        response.setStatusCode(404);
        response.setBody("{\"error\": \"Not Found\", \"message\": \"Endpoint not found\"}");
        return response;
    }
    
    private APIGatewayProxyResponseEvent createErrorResponse(String errorMessage) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(500);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        
        response.setBody("{\"error\": \"Internal Server Error\", \"message\": \"" + errorMessage + "\"}");
        return response;
    }
}
