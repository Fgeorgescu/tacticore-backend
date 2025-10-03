package com.tacticore.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticore.lambda.model.dto.UserDto;
import com.tacticore.lambda.service.UserService;
import com.tacticore.lambda.service.AnalyticsService;
import com.tacticore.lambda.service.DatabaseMatchService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler completo de Spring Boot para AWS Lambda
 * Incluye todos los servicios y funcionalidades del backend
 */
public class SpringBootLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ConfigurableApplicationContext applicationContext;
    private static ObjectMapper objectMapper;
    private static UserService userService;
    private static AnalyticsService analyticsService;
    private static DatabaseMatchService databaseMatchService;

    static {
        initializeSpringBoot();
    }

    private static void initializeSpringBoot() {
        try {
            System.out.println("🚀 Initializing Spring Boot for Lambda...");
            
            // Configurar propiedades del sistema para Lambda
            System.setProperty("spring.profiles.active", "lambda");
            System.setProperty("spring.main.web-application-type", "none");
            System.setProperty("spring.main.lazy-initialization", "true");
            
            // Inicializar Spring Boot
            applicationContext = SpringApplication.run(LambdaApplication.class);
            
            // Obtener beans necesarios
            objectMapper = applicationContext.getBean(ObjectMapper.class);
            userService = applicationContext.getBean(UserService.class);
            analyticsService = applicationContext.getBean(AnalyticsService.class);
            databaseMatchService = applicationContext.getBean(DatabaseMatchService.class);
            
            System.out.println("✅ Spring Boot initialized successfully for Lambda");
        } catch (Exception e) {
            System.err.println("❌ Error initializing Spring Boot for Lambda: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot", e);
        }
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        System.out.println("📨 Received request: " + input.getHttpMethod() + " " + input.getPath());

        try {
            // Crear respuesta base
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            
            // Headers CORS
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

            // Routing de endpoints
            String path = input.getPath();
            String method = input.getHttpMethod();

            if ("/api/health".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleHealthCheck(response);
            } else if ("/api/users".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetAllUsers(response);
            } else if (path.matches("/api/users/[^/]+") && HttpMethod.GET.name().equals(method)) {
                return handleGetUserByName(input, response);
            } else if ("/api/users/search".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleSearchUsers(input, response);
            } else if ("/api/users/roles".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetRoles(response);
            } else if ("/api/matches".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetMatches(input, response);
            } else if ("/api/dashboard/stats".equals(path) && HttpMethod.GET.name().equals(method)) {
                return handleGetDashboardStats(input, response);
            } else {
                return handleNotFound(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Error handling request: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Internal server error: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleHealthCheck(APIGatewayProxyResponseEvent response) {
        response.setStatusCode(200);
        response.setBody("{\"status\": \"healthy\", \"message\": \"Tacti-Core Backend is running on Lambda!\", \"timestamp\": " + System.currentTimeMillis() + "}");
        return response;
    }

    private APIGatewayProxyResponseEvent handleGetAllUsers(APIGatewayProxyResponseEvent response) {
        try {
            List<UserDto> users = userService.convertToDtoList(userService.getAllUsers());
            String usersJson = objectMapper.writeValueAsString(users);
            
            response.setStatusCode(200);
            response.setBody(usersJson);
            System.out.println("✅ Returned " + users.size() + " users");
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error getting all users: " + e.getMessage());
            return createErrorResponse("Error getting users: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleGetUserByName(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response) {
        try {
            String path = input.getPath();
            String userName = path.substring(path.lastIndexOf('/') + 1);
            
            var userOpt = userService.getUserByName(userName);
            
            if (userOpt.isPresent()) {
                UserDto userDto = userService.convertToDto(userOpt.get());
                String userJson = objectMapper.writeValueAsString(userDto);
                response.setStatusCode(200);
                response.setBody(userJson);
                System.out.println("✅ Found user: " + userName);
            } else {
                response.setStatusCode(404);
                response.setBody("{\"error\":\"User not found\",\"message\":\"User '" + userName + "' does not exist\"}");
                System.out.println("❌ User not found: " + userName);
            }
            
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error getting user by name: " + e.getMessage());
            return createErrorResponse("Error getting user: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleSearchUsers(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response) {
        try {
            String name = input.getQueryStringParameters() != null ?
                         input.getQueryStringParameters().get("name") : "";
            
            if (name == null || name.length() < 2) {
                response.setStatusCode(200);
                response.setBody("[]");
                return response;
            }
            
            List<UserDto> users = userService.convertToDtoList(userService.searchUsersByName(name));
            String searchResults = objectMapper.writeValueAsString(users);
            
            response.setStatusCode(200);
            response.setBody(searchResults);
            System.out.println("✅ Search for '" + name + "' returned " + users.size() + " users");
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error searching users: " + e.getMessage());
            return createErrorResponse("Error searching users: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleGetRoles(APIGatewayProxyResponseEvent response) {
        try {
            String[] roles = userService.getAvailableRoles();
            String rolesJson = objectMapper.writeValueAsString(roles);
            
            response.setStatusCode(200);
            response.setBody(rolesJson);
            System.out.println("✅ Returned " + roles.length + " roles");
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error getting roles: " + e.getMessage());
            return createErrorResponse("Error getting roles: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleGetMatches(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response) {
        try {
            String user = input.getQueryStringParameters() != null ?
                         input.getQueryStringParameters().get("user") : null;
            
            var matches = (user != null && !user.isEmpty()) ?
                         databaseMatchService.getMatchesByUser(user) :
                         databaseMatchService.getAllMatches();
            
            String matchesJson = objectMapper.writeValueAsString(matches);
            
            response.setStatusCode(200);
            response.setBody(matchesJson);
            System.out.println("✅ Returned " + matches.size() + " matches for user: " + (user != null ? user : "all"));
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error getting matches: " + e.getMessage());
            return createErrorResponse("Error getting matches: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent handleGetDashboardStats(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response) {
        try {
            String user = input.getQueryStringParameters() != null ?
                         input.getQueryStringParameters().get("user") : null;
            
            var stats = analyticsService.getDashboardStats(user);
            String statsJson = objectMapper.writeValueAsString(stats);
            
            response.setStatusCode(200);
            response.setBody(statsJson);
            System.out.println("✅ Returned dashboard stats for user: " + (user != null ? user : "all"));
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error getting dashboard stats: " + e.getMessage());
            return createErrorResponse("Error getting dashboard stats: " + e.getMessage());
        }
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
