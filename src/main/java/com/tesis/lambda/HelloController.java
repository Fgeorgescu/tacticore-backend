package com.tesis.lambda;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("Hello World from Spring Boot!");
        return "Hello World from Spring Boot!";
    }
}
