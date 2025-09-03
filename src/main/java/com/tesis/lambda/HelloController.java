package com.tesis.lambda;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("Hello World from Spring Boot!");
        return "Hello World from Spring Boot!";
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "tacticore-backend-java");
        return response;
    }
}
