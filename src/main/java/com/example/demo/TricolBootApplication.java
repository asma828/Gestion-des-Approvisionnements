package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TricolBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TricolBootApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Tricol Application Started Successfully!");
        System.out.println("========================================");
        System.out.println("Swagger UI: http://localhost:8081/swagger-ui.html");
        System.out.println("API Docs: http://localhost:8081/api-docs");
        System.out.println("========================================\n");
    }
}