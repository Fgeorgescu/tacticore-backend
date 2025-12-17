package com.tacticore.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.tacticore.lambda.controller.ApiController;
import com.tacticore.lambda.controller.DataController;
import com.tacticore.lambda.controller.KillAnalysisController;
import com.tacticore.lambda.controller.MatchController;
import com.tacticore.lambda.controller.UserController;
import com.tacticore.lambda.model.S3MatchUploadRequest;
import com.tacticore.lambda.model.MatchResponse;

/**
 * Handler completo de Spring Boot para AWS Lambda
 * Usa DispatcherServlet para exponer todos los endpoints automáticamente
 */
public class SpringBootLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ConfigurableApplicationContext applicationContext;
    private static DispatcherServlet dispatcherServlet;

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
            
            // No usamos DispatcherServlet - manejamos las rutas directamente
            dispatcherServlet = null;
            
            System.out.println("✅ Spring Boot initialized successfully for Lambda");
        } catch (Exception e) {
            System.err.println("❌ Error initializing Spring Boot for Lambda: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot", e);
        }
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String method = input.getHttpMethod();
        String path = normalizePath(input.getPath());
        System.out.println("📨 Received request: " + method + " " + path);

        try {
            // Obtener todos los controladores
            ApiController apiController = applicationContext.getBean(ApiController.class);
            HelloController helloController = applicationContext.getBean(HelloController.class);
            MatchController matchController = applicationContext.getBean(MatchController.class);
            UserController userController = applicationContext.getBean(UserController.class);
            KillAnalysisController analysisController = applicationContext.getBean(KillAnalysisController.class);
            DataController dataController = applicationContext.getBean(DataController.class);
            
            // Extraer parámetros de query
            Map<String, String> queryParams = input.getQueryStringParameters();
            String requestBody = input.getBody();
            
            // Router simple basado en path y método
            Object result = routeRequest(apiController, helloController, matchController, 
                userController, analysisController, dataController, method, path, queryParams, requestBody);
            
            // Convertir resultado a JSON
            String body;
            if (result instanceof String) {
                body = (String) result;
            } else {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                body = mapper.writeValueAsString(result);
            }
            
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody(body);
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.put("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.setHeaders(headers);
            
            System.out.println("✅ Response status: 200");
            return response;

        } catch (Exception e) {
            System.err.println("❌ Error handling request: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Internal server error: " + e.getMessage());
        }
    }
    
    private Object routeRequest(ApiController apiController, HelloController helloController,
                                MatchController matchController, UserController userController,
                                KillAnalysisController analysisController, DataController dataController,
                                String method, String path, Map<String, String> queryParams, 
                                String requestBody) throws Exception {
        String userParam = queryParams != null ? queryParams.get("user") : null;
        
        // Patrones para extraer IDs de los paths
        java.util.regex.Pattern matchIdPattern = java.util.regex.Pattern.compile("/api/matches/([^/]+)(?:/(.*))?");
        java.util.regex.Pattern userNamePattern = java.util.regex.Pattern.compile("/api/users/([^/]+)(?:/(.*))?");
        java.util.regex.Pattern analysisPattern = java.util.regex.Pattern.compile("/api/analysis/(.+)");
        
        java.util.regex.Matcher matchIdMatcher = matchIdPattern.matcher(path);
        java.util.regex.Matcher userNameMatcher = userNamePattern.matcher(path);
        java.util.regex.Matcher analysisMatcher = analysisPattern.matcher(path);
        
        // ==================== GET / HEAD ====================
        if ("GET".equals(method) || "HEAD".equals(method)) {
            
            // --- Health & Ping ---
            if ("/ping".equals(path)) {
                return helloController.ping();
            }
            if ("/hello".equals(path)) {
                return helloController.hello();
            }
            if ("/api/health".equals(path)) {
                return java.util.Map.of("status", "healthy", "message", "Tacti-Core Backend is running!");
            }
            
            // --- Game Data ---
            if ("/api/maps".equals(path)) {
                return apiController.getMaps().getBody();
            }
            if ("/api/weapons".equals(path)) {
                return apiController.getWeapons().getBody();
            }
            
            // --- Analytics ---
            if ("/api/analytics/dashboard".equals(path)) {
                return apiController.getDashboardStats(userParam).getBody();
            }
            if ("/api/analytics/historical".equals(path)) {
                String timeRange = queryParams != null ? queryParams.get("timeRange") : "all";
                String metric = queryParams != null ? queryParams.get("metric") : "kdr";
                return apiController.getHistoricalAnalytics(
                    timeRange != null ? timeRange : "all",
                    metric != null ? metric : "kdr"
                ).getBody();
            }
            
            // --- Data Management ---
            if ("/api/data/status".equals(path)) {
                return dataController.getDataStatus().getBody();
            }
            
            // --- Users ---
            if ("/api/users".equals(path)) {
                return userController.getAllUsers().getBody();
            }
            if ("/api/users/roles".equals(path)) {
                return userController.getAvailableRoles().getBody();
            }
            if ("/api/users/stats".equals(path)) {
                return userController.getUserStatistics().getBody();
            }
            if ("/api/users/top/score".equals(path)) {
                return userController.getTopPlayersByScore().getBody();
            }
            if ("/api/users/top/kills".equals(path)) {
                return userController.getTopPlayersByKills().getBody();
            }
            if ("/api/users/top/kdr".equals(path)) {
                return userController.getTopPlayersByKDR().getBody();
            }
            if ("/api/users/top/matches".equals(path)) {
                int minMatches = queryParams != null && queryParams.get("minMatches") != null 
                    ? Integer.parseInt(queryParams.get("minMatches")) : 5;
                return userController.getTopPlayersByMatches(minMatches).getBody();
            }
            if ("/api/users/search".equals(path)) {
                String name = queryParams != null ? queryParams.get("name") : "";
                return userController.searchUsers(name).getBody();
            }
            if ("/api/users/debug/kills-users".equals(path)) {
                return userController.getKillsUsers().getBody();
            }
            
            // Users con parámetros en path
            if (path.matches("/api/users/exists/[^/]+")) {
                String userName = path.substring("/api/users/exists/".length());
                return userController.userExists(userName).getBody();
            }
            if (path.matches("/api/users/role/[^/]+")) {
                String role = path.substring("/api/users/role/".length());
                return userController.getUsersByRole(role).getBody();
            }
            if (path.matches("/api/users/debug/[^/]+/real-stats")) {
                String userName = path.replaceAll("/api/users/debug/([^/]+)/real-stats", "$1");
                return userController.getRealUserStats(userName).getBody();
            }
            if (path.matches("/api/users/[^/]+/profile")) {
                String userName = path.replaceAll("/api/users/([^/]+)/profile", "$1");
                return userController.getUserProfile(userName).getBody();
            }
            // GET /api/users/{name} - Debe ir al final para no interferir con otras rutas
            if (path.matches("/api/users/[^/]+")) {
                String userName = path.substring("/api/users/".length());
                // Excluir rutas especiales que ya fueron manejadas arriba
                if (!userName.equals("roles") && !userName.equals("stats") && 
                    !userName.equals("search") && !userName.equals("debug") &&
                    !userName.startsWith("top/") && !userName.startsWith("exists/") &&
                    !userName.startsWith("role/") && !userName.contains("/")) {
                    return userController.getUserByName(userName).getBody();
                }
            }
            
            // --- Kill Analysis ---
            if ("/api/analysis/overview".equals(path)) {
                return analysisController.getOverallAnalysis().getBody();
            }
            if ("/api/analysis/players".equals(path)) {
                return analysisController.getAllPlayersAnalysis().getBody();
            }
            if ("/api/analysis/rounds".equals(path)) {
                return analysisController.getAllRoundsAnalysis().getBody();
            }
            if ("/api/analysis/users".equals(path)) {
                return analysisController.getAllUsers().getBody();
            }
            if (path.matches("/api/analysis/player/[^/]+")) {
                String playerName = path.substring("/api/analysis/player/".length());
                return analysisController.getPlayerStats(playerName).getBody();
            }
            if (path.matches("/api/analysis/round/[0-9]+")) {
                int roundNumber = Integer.parseInt(path.substring("/api/analysis/round/".length()));
                return analysisController.getRoundAnalysis(roundNumber).getBody();
            }
            if (path.matches("/api/analysis/user/[^/]+/overview")) {
                String user = path.replaceAll("/api/analysis/user/([^/]+)/overview", "$1");
                return analysisController.getUserAnalysis(user).getBody();
            }
            if (path.matches("/api/analysis/user/[^/]+/kills")) {
                String user = path.replaceAll("/api/analysis/user/([^/]+)/kills", "$1");
                return analysisController.getUserKills(user).getBody();
            }
            if (path.matches("/api/analysis/user/[^/]+/round/[0-9]+")) {
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("/api/analysis/user/([^/]+)/round/([0-9]+)");
                java.util.regex.Matcher m = p.matcher(path);
                if (m.matches()) {
                    return analysisController.getUserKillsByRound(m.group(1), Integer.parseInt(m.group(2))).getBody();
                }
            }
            
            // --- Matches ---
            if ("/api/matches".equals(path)) {
                return apiController.getMatches(userParam).getBody();
            }
            if (matchIdMatcher.matches()) {
                String matchId = matchIdMatcher.group(1);
                String subPath = matchIdMatcher.group(2);
                
                if (subPath == null || subPath.isEmpty()) {
                    return apiController.getMatch(matchId).getBody();
                }
                if ("kills".equals(subPath)) {
                    return apiController.getMatchKills(matchId, userParam).getBody();
                }
                if ("chat".equals(subPath)) {
                    return apiController.getMatchChat(matchId).getBody();
                }
                if ("status".equals(subPath)) {
                    return matchController.getMatchStatus(matchId).getBody();
                }
            }
        }
        
        // ==================== POST ====================
        if ("POST".equals(method)) {
            if ("/api/matches/s3".equals(path)) {
                return handleS3MatchUpload(matchController, requestBody);
            }
            if ("/api/users".equals(path)) {
                return handleCreateUser(userController, requestBody);
            }
            if ("/api/data/load".equals(path)) {
                String fileName = "example.json"; // Default
                return dataController.loadData(fileName).getBody();
            }
            if ("/api/data/reload-preloaded".equals(path)) {
                return dataController.reloadPreloadedData().getBody();
            }
            if ("/api/data/reload-dummy".equals(path)) {
                return dataController.reloadDummyData().getBody();
            }
            if ("/api/users/debug/reload-kills".equals(path)) {
                return userController.reloadKills().getBody();
            }
            if ("/api/users/debug/update-stats".equals(path)) {
                return userController.updateUserStats().getBody();
            }
            // POST chat message
            if (path.matches("/api/matches/[^/]+/chat")) {
                String matchId = path.replaceAll("/api/matches/([^/]+)/chat", "$1");
                return handleChatMessage(apiController, matchId, requestBody);
            }
        }
        
        // ==================== DELETE ====================
        if ("DELETE".equals(method)) {
            if ("/api/data/clear".equals(path)) {
                return dataController.clearData().getBody();
            }
            if (path.matches("/api/matches/[^/]+")) {
                String matchId = path.substring("/api/matches/".length());
                return apiController.deleteMatch(matchId).getBody();
            }
        }
        
        throw new IllegalArgumentException("Route not found: " + method + " " + path);
    }
    
    /**
     * Maneja el upload de match desde S3
     */
    private Object handleS3MatchUpload(MatchController matchController, String requestBody) throws Exception {
        if (requestBody == null || requestBody.trim().isEmpty()) {
            return java.util.Map.of("error", "Request body is required");
        }
        
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        S3MatchUploadRequest request = mapper.readValue(requestBody, S3MatchUploadRequest.class);
        
        org.springframework.http.ResponseEntity<MatchResponse> response = matchController.uploadMatchFromS3(request);
        return response.getBody();
    }
    
    /**
     * Maneja la creación de usuarios
     */
    private Object handleCreateUser(UserController userController, String requestBody) throws Exception {
        if (requestBody == null || requestBody.trim().isEmpty()) {
            return java.util.Map.of("error", "Request body is required");
        }
        
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        UserController.CreateUserRequest request = mapper.readValue(requestBody, UserController.CreateUserRequest.class);
        
        return userController.createOrGetUser(request).getBody();
    }
    
    /**
     * Maneja el envío de mensajes de chat
     */
    private Object handleChatMessage(ApiController apiController, String matchId, String requestBody) throws Exception {
        if (requestBody == null || requestBody.trim().isEmpty()) {
            return java.util.Map.of("error", "Request body is required");
        }
        
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> request = mapper.readValue(requestBody, java.util.Map.class);
        
        return apiController.sendChatMessage(matchId, request).getBody();
    }

    /**
     * Normaliza el path del request, manejando casos donde viene con prefijo del stage
     */
    private String normalizePath(String path) {
        if (path == null) {
            return "/";
        }
        
        // Remover prefijo del stage si existe (ej: /prod/api/... -> /api/...)
        path = path.replaceFirst("^/(prod|dev|test|staging)/", "/");
        
        // Asegurar que empiece con /
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        return path;
    }

    /**
     * Crea un HttpServletRequest desde el evento de API Gateway
     */
    private HttpServletRequest createHttpRequest(APIGatewayProxyRequestEvent input, String path) {
        return new LambdaHttpServletRequest(input, path);
    }

    /**
     * Convierte LambdaHttpServletResponse a APIGatewayProxyResponseEvent
     */
    private APIGatewayProxyResponseEvent convertToApiGatewayResponse(LambdaHttpServletResponse servletResponse) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        
        // Status code
        response.setStatusCode(servletResponse.getStatus());
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        
        // Headers CORS por defecto
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        
        // Agregar headers de la respuesta
        servletResponse.getHeaderNames().forEach(headerName -> {
            String headerValue = servletResponse.getHeader(headerName);
            if (headerValue != null) {
                headers.put(headerName, headerValue);
            }
        });
        
        // Asegurar Content-Type
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/json");
        }
        
        response.setHeaders(headers);
        
        // Body
        String body = servletResponse.getContentAsString();
        response.setBody(body != null ? body : "");
        
        return response;
    }

    /**
     * Crea una respuesta de error
     */
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

    /**
     * Mock ServletContext para Lambda
     */
    private static class MockServletContext implements ServletContext {
        private final Map<String, Object> attributes = new HashMap<>();
        
        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public ServletContext getContext(String uripath) {
            return this;
        }

        @Override
        public int getMajorVersion() {
            return 5;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public int getEffectiveMajorVersion() {
            return 5;
        }

        @Override
        public int getEffectiveMinorVersion() {
            return 0;
        }

        @Override
        public String getMimeType(String file) {
            return null;
        }

        @Override
        public java.util.Set<String> getResourcePaths(String path) {
            return null;
        }

        @Override
        public java.net.URL getResource(String path) {
            return null;
        }

        @Override
        public java.io.InputStream getResourceAsStream(String path) {
            return null;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getNamedDispatcher(String name) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
            return null;
        }

        @Override
        public void log(String msg) {
            System.out.println("ServletContext: " + msg);
        }

        @Override
        public void log(String message, Throwable throwable) {
            System.err.println("ServletContext: " + message);
            throwable.printStackTrace();
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public String getServerInfo() {
            return "Lambda/1.0";
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getInitParameterNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public boolean setInitParameter(String name, String value) {
            return false;
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return java.util.Collections.enumeration(attributes.keySet());
        }

        @Override
        public void setAttribute(String name, Object object) {
            attributes.put(name, object);
        }

        @Override
        public void removeAttribute(String name) {
            // No-op
        }

        @Override
        public String getServletContextName() {
            return "Lambda";
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, jakarta.servlet.Servlet servlet) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends jakarta.servlet.Servlet> servletClass) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.Servlet> T createServlet(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.ServletRegistration getServletRegistration(String servletName) {
            return null;
        }

        @Override
        public java.util.Map<String, ? extends jakarta.servlet.ServletRegistration> getServletRegistrations() {
            return new HashMap<>();
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, jakarta.servlet.Filter filter) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends jakarta.servlet.Filter> filterClass) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.Filter> T createFilter(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.FilterRegistration getFilterRegistration(String filterName) {
            return null;
        }

        @Override
        public java.util.Map<String, ? extends jakarta.servlet.FilterRegistration> getFilterRegistrations() {
            return new HashMap<>();
        }

        @Override
        public jakarta.servlet.SessionCookieConfig getSessionCookieConfig() {
            return null;
        }

        @Override
        public void setSessionTrackingModes(java.util.Set<jakarta.servlet.SessionTrackingMode> sessionTrackingModes) {
            // No-op
        }

        @Override
        public java.util.Set<jakarta.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() {
            return null;
        }

        @Override
        public java.util.Set<jakarta.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() {
            return null;
        }

        @Override
        public void addListener(String className) {
            // No-op
        }

        @Override
        public <T extends java.util.EventListener> void addListener(T t) {
            // No-op
        }

        @Override
        public void addListener(Class<? extends java.util.EventListener> listenerClass) {
            // No-op
        }

        @Override
        public <T extends java.util.EventListener> T createListener(Class<T> clazz) {
            return null;
        }

        @Override
        public jakarta.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void declareRoles(String... roleNames) {
            // No-op
        }

        @Override
        public String getVirtualServerName() {
            return null;
        }

        @Override
        public int getSessionTimeout() {
            return 0;
        }

        @Override
        public void setSessionTimeout(int sessionTimeout) {
            // No-op
        }

        @Override
        public String getRequestCharacterEncoding() {
            return null;
        }

        @Override
        public void setRequestCharacterEncoding(String encoding) {
            // No-op
        }

        @Override
        public String getResponseCharacterEncoding() {
            return null;
        }

        @Override
        public void setResponseCharacterEncoding(String encoding) {
            // No-op
        }
    }

    /**
     * Mock ServletConfig para Lambda
     */
    private static class MockServletConfig implements jakarta.servlet.ServletConfig {
        private final ServletContext servletContext;

        public MockServletConfig(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public String getServletName() {
            return "DispatcherServlet";
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getInitParameterNames() {
            return java.util.Collections.emptyEnumeration();
        }
    }

    /**
     * Implementación simple de HttpServletRequest para Lambda
     */
    private static class LambdaHttpServletRequest implements HttpServletRequest {
        private final APIGatewayProxyRequestEvent input;
        private final String path;
        private final Map<String, Object> attributes = new HashMap<>();
        private final Map<String, String[]> parameters = new HashMap<>();
        private byte[] bodyBytes;

        public LambdaHttpServletRequest(APIGatewayProxyRequestEvent input, String path) {
            this.input = input;
            this.path = path;
            
            // Parsear query parameters
            if (input.getQueryStringParameters() != null) {
                input.getQueryStringParameters().forEach((key, value) -> {
                    parameters.put(key, new String[]{value});
                });
            }
            
            // Parsear body
            if (input.getBody() != null && !input.getBody().isEmpty()) {
                this.bodyBytes = input.getBody().getBytes();
            }
        }

        @Override
        public String getMethod() {
            return input.getHttpMethod() != null ? input.getHttpMethod() : "GET";
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return path;
        }

        @Override
        public String getServletPath() {
            return path;
        }

        @Override
        public String getPathInfo() {
            return path;
        }

        @Override
        public String getQueryString() {
            if (input.getQueryStringParameters() == null || input.getQueryStringParameters().isEmpty()) {
                return null;
            }
            return input.getQueryStringParameters().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        }

        @Override
        public String getParameter(String name) {
            String[] values = parameters.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            return java.util.Collections.enumeration(parameters.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameters.get(name);
        }

        @Override
        public java.util.Map<String, String[]> getParameterMap() {
            return new HashMap<>(parameters);
        }

        @Override
        public String getHeader(String name) {
            return input.getHeaders() != null ? input.getHeaders().get(name) : null;
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            return input.getHeaders() != null 
                ? java.util.Collections.enumeration(input.getHeaders().keySet())
                : java.util.Collections.emptyEnumeration();
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            String value = getHeader(name);
            return value != null 
                ? java.util.Collections.enumeration(java.util.Arrays.asList(value))
                : java.util.Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(String name) {
            String value = getHeader(name);
            return value != null ? Integer.parseInt(value) : -1;
        }

        @Override
        public long getDateHeader(String name) {
            String value = getHeader(name);
            return value != null ? Long.parseLong(value) : -1;
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return java.util.Collections.enumeration(attributes.keySet());
        }

        @Override
        public void setAttribute(String name, Object o) {
            attributes.put(name, o);
        }

        @Override
        public void removeAttribute(String name) {
            attributes.remove(name);
        }

        @Override
        public String getContentType() {
            return input.getHeaders() != null && input.getHeaders().containsKey("Content-Type")
                ? input.getHeaders().get("Content-Type")
                : "application/json";
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(String env) {
            // No-op for Lambda
        }

        @Override
        public int getContentLength() {
            return bodyBytes != null ? bodyBytes.length : -1;
        }

        @Override
        public long getContentLengthLong() {
            return bodyBytes != null ? bodyBytes.length : -1;
        }

        @Override
        public java.io.BufferedReader getReader() throws IOException {
            String body = input.getBody() != null ? input.getBody() : "";
            return new java.io.BufferedReader(new java.io.StringReader(body));
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() throws IOException {
            final java.io.ByteArrayInputStream inputStream = bodyBytes != null 
                ? new java.io.ByteArrayInputStream(bodyBytes)
                : new java.io.ByteArrayInputStream(new byte[0]);
            
            return new jakarta.servlet.ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return inputStream.read();
                }
                
                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }
                
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setReadListener(jakarta.servlet.ReadListener readListener) {
                    // No-op
                }
            };
        }

        @Override
        public jakarta.servlet.http.Part getPart(String name) throws IOException, jakarta.servlet.ServletException {
            return null;
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() throws IOException, jakarta.servlet.ServletException {
            return java.util.Collections.emptyList();
        }

        // Métodos restantes con implementaciones básicas
        @Override public String getAuthType() { return null; }
        @Override public jakarta.servlet.http.Cookie[] getCookies() { return new jakarta.servlet.http.Cookie[0]; }
        @Override public String getRemoteUser() { return null; }
        @Override public boolean isUserInRole(String role) { return false; }
        @Override public java.security.Principal getUserPrincipal() { return null; }
        @Override public String getRequestedSessionId() { return null; }
        @Override public StringBuffer getRequestURL() { return new StringBuffer("https://lambda/" + path); }
        @Override public String getProtocol() { return "HTTP/1.1"; }
        @Override public String getScheme() { return "https"; }
        @Override public String getServerName() { return "lambda"; }
        @Override public int getServerPort() { return 443; }
        @Override public String getRemoteAddr() { return "127.0.0.1"; }
        @Override public String getRemoteHost() { return "lambda"; }
        @Override public Locale getLocale() { return Locale.getDefault(); }
        @Override public java.util.Enumeration<Locale> getLocales() { return java.util.Collections.enumeration(java.util.Arrays.asList(Locale.getDefault())); }
        @Override public boolean isSecure() { return true; }
        @Override public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public int getRemotePort() { return 0; }
        @Override public String getLocalName() { return "lambda"; }
        @Override public String getLocalAddr() { return "127.0.0.1"; }
        @Override public int getLocalPort() { return 443; }
        @Override public ServletContext getServletContext() { return null; }
        @Override public jakarta.servlet.ServletConnection getServletConnection() { return null; }
        @Override public String getProtocolRequestId() { return null; }
        @Override public String getRequestId() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public jakarta.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public jakarta.servlet.DispatcherType getDispatcherType() { return jakarta.servlet.DispatcherType.REQUEST; }
        @Override public boolean isRequestedSessionIdValid() { return false; }
        @Override public boolean isRequestedSessionIdFromCookie() { return false; }
        @Override public boolean isRequestedSessionIdFromURL() { return false; }
        @Override public jakarta.servlet.http.HttpSession getSession(boolean create) { return null; }
        @Override public jakarta.servlet.http.HttpSession getSession() { return null; }
        @Override public String changeSessionId() { return null; }
        @Override public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
        @Override public void logout() throws jakarta.servlet.ServletException {}
        @Override public void login(String username, String password) throws jakarta.servlet.ServletException {}
        @Override public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) throws IOException, jakarta.servlet.ServletException { return false; }
    }

    /**
     * Implementación simple de HttpServletResponse para Lambda
     */
    private static class LambdaHttpServletResponse implements HttpServletResponse {
        private int status = 200;
        private final Map<String, String> headers = new HashMap<>();
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private PrintWriter writer;
        private String characterEncoding = "UTF-8";
        private String contentType = "application/json";

        @Override
        public void setStatus(int sc) {
            this.status = sc;
        }


        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            String existing = headers.get(name);
            if (existing != null) {
                headers.put(name, existing + ", " + value);
            } else {
                headers.put(name, value);
            }
        }

        @Override
        public void setIntHeader(String name, int value) {
            headers.put(name, String.valueOf(value));
        }

        @Override
        public void addIntHeader(String name, int value) {
            addHeader(name, String.valueOf(value));
        }

        @Override
        public void setDateHeader(String name, long date) {
            headers.put(name, String.valueOf(date));
        }

        @Override
        public void addDateHeader(String name, long date) {
            addHeader(name, String.valueOf(date));
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            String value = headers.get(name);
            return value != null ? java.util.Arrays.asList(value) : java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public void setContentType(String type) {
            this.contentType = type;
            headers.put("Content-Type", type);
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public void setCharacterEncoding(String charset) {
            this.characterEncoding = charset;
        }

        @Override
        public String getCharacterEncoding() {
            return characterEncoding;
        }

        @Override
        public java.io.PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PrintWriter(new java.io.OutputStreamWriter(getOutputStream(), characterEncoding));
            }
            return writer;
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
            return new jakarta.servlet.ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }
                
                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                    // No-op
                }
            };
        }

        public String getContentAsString() {
            try {
                if (writer != null) {
                    writer.flush();
                }
                return outputStream.toString(characterEncoding);
            } catch (UnsupportedEncodingException e) {
                return outputStream.toString();
            }
        }

        // Métodos restantes con implementaciones básicas
        @Override public void addCookie(jakarta.servlet.http.Cookie cookie) {}
        @Override public boolean containsHeader(String name) { return headers.containsKey(name); }
        @Override public String encodeURL(String url) { return url; }
        @Override public String encodeRedirectURL(String url) { return url; }
        @Override public void sendError(int sc, String msg) throws IOException { setStatus(sc); }
        @Override public void sendError(int sc) throws IOException { setStatus(sc); }
        @Override public void sendRedirect(String location) throws IOException { setStatus(302); setHeader("Location", location); }
        @Override public void setContentLength(int len) { setIntHeader("Content-Length", len); }
        @Override public void setContentLengthLong(long len) { setHeader("Content-Length", String.valueOf(len)); }
        @Override public void setBufferSize(int size) {}
        @Override public int getBufferSize() { return 8192; }
        @Override public void flushBuffer() throws IOException { if (writer != null) writer.flush(); }
        @Override public void resetBuffer() { outputStream.reset(); }
        @Override public boolean isCommitted() { return false; }
        @Override public void reset() { status = 200; headers.clear(); outputStream.reset(); }
        @Override public void setLocale(Locale loc) {}
        @Override public Locale getLocale() { return Locale.getDefault(); }
    }
}
