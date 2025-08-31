package com.tesis.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
        System.out.println("Hello World from AWS Lambda!");
        
        // Crear respuesta
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        
        // Body
        String body = "{\"message\": \"Hello World from AWS Lambda!\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";
        response.setBody(body);
        
        return response;
    }
}
