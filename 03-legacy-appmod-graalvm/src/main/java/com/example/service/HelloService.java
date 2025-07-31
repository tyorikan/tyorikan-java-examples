package com.example.service;

import org.springframework.stereotype.Service;

/**
 * Service class for handling hello message generation.
 * Migrated from the legacy Struts2 application to Spring Boot.
 */
@Service
public class HelloService {

    /**
     * Generates a greeting message based on the provided name.
     * 
     * @param name the name to include in the greeting
     * @return a personalized greeting message
     */
    public String getMessage(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "!";
    }
    
    /**
     * Generates a more detailed greeting message with additional information.
     * 
     * @param name the name to include in the greeting
     * @return a detailed greeting message
     */
    public String getDetailedMessage(String name) {
        String basicMessage = getMessage(name);
        return basicMessage + " Welcome to the modernized Spring Boot application with GraalVM support!";
    }
}
