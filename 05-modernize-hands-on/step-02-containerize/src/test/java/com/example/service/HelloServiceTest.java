package com.example.service;

import org.junit.Test;
import static org.junit.Assert.*;

public class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    public void testGetMessage() {
        assertEquals("Hello, World!", helloService.getMessage(null));
        assertEquals("Hello, World!", helloService.getMessage(""));
        assertEquals("Hello, World!", helloService.getMessage(" "));
        assertEquals("Hello, Gemini!", helloService.getMessage("Gemini"));
    }
}
