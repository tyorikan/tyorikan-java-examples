package com.example.controller;

import com.example.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web controller for handling hello requests.
 * Replaces the Struts2 HelloAction with Spring MVC controller.
 */
@Controller
public class HelloController {

    private final HelloService helloService;

    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * Displays the main page with a form to enter a name.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Handles the hello form submission and displays the greeting.
     */
    @PostMapping("/hello")
    public String hello(@RequestParam(value = "name", required = false) String name, Model model) {
        String message = helloService.getMessage(name);
        String detailedMessage = helloService.getDetailedMessage(name);
        
        model.addAttribute("message", message);
        model.addAttribute("detailedMessage", detailedMessage);
        model.addAttribute("name", name);
        
        return "hello";
    }

    /**
     * REST API endpoint for getting hello message as JSON.
     * Useful for API clients and testing.
     */
    @GetMapping("/api/hello")
    @ResponseBody
    public HelloResponse getHelloMessage(@RequestParam(value = "name", required = false) String name) {
        String message = helloService.getMessage(name);
        String detailedMessage = helloService.getDetailedMessage(name);
        
        return new HelloResponse(message, detailedMessage, name);
    }

    /**
     * Health check endpoint for container orchestration.
     */
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    /**
     * Response class for JSON API responses.
     */
    public static class HelloResponse {
        private final String message;
        private final String detailedMessage;
        private final String name;

        public HelloResponse(String message, String detailedMessage, String name) {
            this.message = message;
            this.detailedMessage = detailedMessage;
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public String getDetailedMessage() {
            return detailedMessage;
        }

        public String getName() {
            return name;
        }
    }
}
