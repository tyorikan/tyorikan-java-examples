package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the modernized legacy application.
 * This application replaces the Tomcat-based Struts2 application with a 
 * modern Spring Boot application optimized for GraalVM native image compilation.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
