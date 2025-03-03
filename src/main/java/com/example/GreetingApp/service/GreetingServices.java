package com.example.GreetingApp.service;
import org.springframework.stereotype.Service;

@Service
public class GreetingServices {
    public String getSimpleGreeting() {
        return "Hello World";
    }
}
