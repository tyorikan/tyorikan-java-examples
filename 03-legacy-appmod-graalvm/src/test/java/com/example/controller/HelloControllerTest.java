package com.example.controller;

import com.example.service.HelloService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HelloController class using MockMvc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("HelloController Tests")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloService helloService;

    @Test
    @DisplayName("Should display index page")
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Should handle hello form submission with name")
    void testHelloFormWithName() throws Exception {
        mockMvc.perform(post("/hello")
                        .param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(view().name("hello"))
                .andExpect(model().attribute("message", "Hello, Alice!"))
                .andExpect(model().attribute("name", "Alice"))
                .andExpect(model().attributeExists("detailedMessage"));
    }

    @Test
    @DisplayName("Should handle hello form submission without name")
    void testHelloFormWithoutName() throws Exception {
        mockMvc.perform(post("/hello"))
                .andExpect(status().isOk())
                .andExpect(view().name("hello"))
                .andExpect(model().attribute("message", "Hello, World!"))
                .andExpect(model().attributeExists("detailedMessage"));
    }

    @Test
    @DisplayName("Should return JSON response for API endpoint")
    void testApiHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/hello")
                        .param("name", "Bob"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Hello, Bob!"))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.detailedMessage").exists());
    }

    @Test
    @DisplayName("Should return JSON response for API endpoint without name")
    void testApiHelloEndpointWithoutName() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Hello, World!"))
                .andExpect(jsonPath("$.detailedMessage").exists());
    }

    @Test
    @DisplayName("Should return OK for health check endpoint")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
