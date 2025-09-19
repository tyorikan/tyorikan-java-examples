package com.example.controller;

import com.example.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/hello")
    public String hello(@RequestParam("name") String name, Model model) {
        String message = helloService.getMessage(name);
        model.addAttribute("message", message);
        return "hello";
    }
}
