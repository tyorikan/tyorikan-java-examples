package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String getMessage(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name + "!";
    }
}
