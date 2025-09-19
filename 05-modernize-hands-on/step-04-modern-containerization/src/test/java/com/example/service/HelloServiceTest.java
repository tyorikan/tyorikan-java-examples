package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloServiceTest {

    private HelloService helloService;

    @BeforeEach
    void setUp() {
        helloService = new HelloService();
    }

    @Test
    void getMessage_withName() {
        String message = helloService.getMessage("Yori");
        assertEquals("Hello, Yori!", message);
    }

    @Test
    void getMessage_withNullName() {
        String message = helloService.getMessage(null);
        assertEquals("Hello, World!", message);
    }

    @Test
    void getMessage_withEmptyName() {
        String message = helloService.getMessage("");
        assertEquals("Hello, World!", message);
    }
}
