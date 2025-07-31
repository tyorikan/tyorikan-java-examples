package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HelloService class.
 */
@DisplayName("HelloService Tests")
class HelloServiceTest {

    private HelloService helloService;

    @BeforeEach
    void setUp() {
        helloService = new HelloService();
    }

    @Test
    @DisplayName("Should return 'Hello, World!' when name is null")
    void testGetMessageWithNullName() {
        String result = helloService.getMessage(null);
        assertEquals("Hello, World!", result);
    }

    @Test
    @DisplayName("Should return 'Hello, World!' when name is empty")
    void testGetMessageWithEmptyName() {
        String result = helloService.getMessage("");
        assertEquals("Hello, World!", result);
    }

    @Test
    @DisplayName("Should return 'Hello, World!' when name is whitespace only")
    void testGetMessageWithWhitespaceOnlyName() {
        String result = helloService.getMessage("   ");
        assertEquals("Hello, World!", result);
    }

    @Test
    @DisplayName("Should return personalized greeting when name is provided")
    void testGetMessageWithValidName() {
        String result = helloService.getMessage("Alice");
        assertEquals("Hello, Alice!", result);
    }

    @Test
    @DisplayName("Should trim whitespace from name")
    void testGetMessageWithNameContainingWhitespace() {
        String result = helloService.getMessage("  Bob  ");
        assertEquals("Hello, Bob!", result);
    }

    @Test
    @DisplayName("Should return detailed message with null name")
    void testGetDetailedMessageWithNullName() {
        String result = helloService.getDetailedMessage(null);
        assertTrue(result.startsWith("Hello, World!"));
        assertTrue(result.contains("Welcome to the modernized Spring Boot application"));
    }

    @Test
    @DisplayName("Should return detailed message with valid name")
    void testGetDetailedMessageWithValidName() {
        String result = helloService.getDetailedMessage("Charlie");
        assertTrue(result.startsWith("Hello, Charlie!"));
        assertTrue(result.contains("Welcome to the modernized Spring Boot application"));
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testGetMessageWithSpecialCharacters() {
        String result = helloService.getMessage("José-María");
        assertEquals("Hello, José-María!", result);
    }

    @Test
    @DisplayName("Should handle numeric characters in name")
    void testGetMessageWithNumericCharacters() {
        String result = helloService.getMessage("User123");
        assertEquals("Hello, User123!", result);
    }
}
