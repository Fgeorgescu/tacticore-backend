package com.tacticore.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.dto.UserDto;
import com.tacticore.lambda.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaHandler {

    private ConfigurableApplicationContext applicationContext;
    private ObjectMapper objectMapper;

    public LambdaHandler() {
        // El contexto será inyectado por LambdaSpringBootHandler
        this.applicationContext = LambdaSpringBootHandler.getApplicationContext();
        this.objectMapper = applicationContext.getBean(ObjectMapper.class);
    }

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
            } else if ("/api/users".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetAllUsers(response, webContext);
            } else if (path.matches("/api/users/[^/]+") && HttpMethod.GET.name().equals(method)) {
                return handleGetUserByName(input, response, webContext);
            } else if ("/api/users/search".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleSearchUsers(input, response, webContext);
            } else if ("/api/users/roles".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetRoles(response, webContext);
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
    
    private APIGatewayProxyResponseEvent handleGetAllUsers(APIGatewayProxyResponseEvent response, 
                                                          WebApplicationContext webContext) {
        try {
            UserService userService = applicationContext.getBean(UserService.class);
            List<UserDto> users = userService.convertToDtoList(userService.getAllUsers());
            String usersJson = objectMapper.writeValueAsString(users);
            
            response.setStatusCode(200);
            response.setBody(usersJson);
            return response;
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal server error\",\"message\":\"" + e.getMessage() + "\"}");
            return response;
        }
    }
    
    private APIGatewayProxyResponseEvent handleGetUserByName(APIGatewayProxyRequestEvent input,
                                                            APIGatewayProxyResponseEvent response, 
                                                            WebApplicationContext webContext) {
        try {
            String path = input.getPath();
            String userName = path.substring(path.lastIndexOf('/') + 1);
            
            UserService userService = applicationContext.getBean(UserService.class);
            var userOpt = userService.getUserByName(userName);
            
            if (userOpt.isPresent()) {
                UserDto userDto = userService.convertToDto(userOpt.get());
                String userJson = objectMapper.writeValueAsString(userDto);
                response.setStatusCode(200);
                response.setBody(userJson);
            } else {
                response.setStatusCode(404);
                response.setBody("{\"error\":\"User not found\",\"message\":\"User '" + userName + "' does not exist\"}");
            }
            
            return response;
        } catch (Exception e) {
            System.err.println("Error getting user by name: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal server error\",\"message\":\"" + e.getMessage() + "\"}");
            return response;
        }
    }
    
    private APIGatewayProxyResponseEvent handleSearchUsers(APIGatewayProxyRequestEvent input,
                                                          APIGatewayProxyResponseEvent response, 
                                                          WebApplicationContext webContext) {
        try {
            String name = input.getQueryStringParameters() != null ? 
                         input.getQueryStringParameters().get("name") : "";
            
            if (name == null || name.length() < 2) {
                response.setStatusCode(200);
                response.setBody("[]");
                return response;
            }
            
            UserService userService = applicationContext.getBean(UserService.class);
            List<UserDto> users = userService.convertToDtoList(userService.searchUsersByName(name));
            String searchResults = objectMapper.writeValueAsString(users);
            
            response.setStatusCode(200);
            response.setBody(searchResults);
            return response;
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal server error\",\"message\":\"" + e.getMessage() + "\"}");
            return response;
        }
    }
    
    private APIGatewayProxyResponseEvent handleGetRoles(APIGatewayProxyResponseEvent response, 
                                                       WebApplicationContext webContext) {
        try {
            UserService userService = applicationContext.getBean(UserService.class);
            String[] roles = userService.getAvailableRoles();
            String rolesJson = objectMapper.writeValueAsString(roles);
            
            response.setStatusCode(200);
            response.setBody(rolesJson);
            return response;
        } catch (Exception e) {
            System.err.println("Error getting roles: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal server error\",\"message\":\"" + e.getMessage() + "\"}");
            return response;
        }
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
