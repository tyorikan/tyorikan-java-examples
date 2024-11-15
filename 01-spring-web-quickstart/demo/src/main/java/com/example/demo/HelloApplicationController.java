package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApplicationController {

    @RequestMapping("/")
    public String helloTest() {
        return "Hello World";
    }
}