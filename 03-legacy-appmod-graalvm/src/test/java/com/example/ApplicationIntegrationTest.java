package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the entire application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Application Integration Tests")
class ApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should start application context successfully")
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
    }

    @Test
    @DisplayName("Should return index page")
    void testIndexPageIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Modernized Legacy Application"));
        assertTrue(response.getBody().contains("Spring Boot + GraalVM"));
    }

    @Test
    @DisplayName("Should return JSON response from API endpoint")
    void testApiEndpointIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/hello?name=Integration", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Hello, Integration!"));
        assertTrue(response.getBody().contains("\"name\":\"Integration\""));
    }

    @Test
    @DisplayName("Should return default message when no name provided")
    void testApiEndpointWithoutName() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/hello", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Hello, World!"));
    }

    @Test
    @DisplayName("Should return OK from health endpoint")
    void testHealthEndpointIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    @DisplayName("Should handle form submission")
    void testFormSubmissionIntegration() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/hello?name=FormTest", null, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Hello, FormTest!"));
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoint")
    void testNonExistentEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/nonexistent", String.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
