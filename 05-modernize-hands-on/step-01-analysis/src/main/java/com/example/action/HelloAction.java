package com.example.action;

import com.example.service.HelloService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class HelloAction extends ActionSupport {

    @Autowired
    private HelloService helloService;

    private String name;
    private String message;

    public String execute() {
        message = helloService.getMessage(name);
        return SUCCESS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
