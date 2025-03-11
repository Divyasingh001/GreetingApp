package com.example.GreetingApp.service;

import com.example.GreetingApp.Interface.IGreetingService;
import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.repository.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GreetingServices implements IGreetingService {

    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public String getSimpleGreeting() {
        return "Hello World!";
    }

    @Override
    public Greeting addGreeting(String message) {
        Greeting greeting = new Greeting(message != null ? message : "Hello World!");
        return greetingRepository.save(greeting);
    }

    @Override
    public Greeting saveGreeting(String firstName, String lastName) {
        String message = "Hello";
        if (firstName != null && lastName != null) {
            message += ", " + firstName + " " + lastName + "!";
        } else if (firstName != null) {
            message += ", " + firstName + "!";
        } else if (lastName != null) {
            message += ", " + lastName + "!";
        } else {
            message += " World!";
        }
        Greeting greeting = new Greeting(message);
        return greetingRepository.save(greeting);
    }

    @Override
    public Optional<Greeting> getGreetingById(Long id) {
        return greetingRepository.findById(id);
    }

    @Override
    public List<Greeting> getAllGreetings() {
        return greetingRepository.findAll();
    }

    @Override
    public Greeting updateGreeting(Long id, String newMessage) {
        return greetingRepository.findById(id)
                .map(greeting -> {
                    greeting.setMessage(newMessage);
                    return greetingRepository.save(greeting);
                })
                .orElseThrow(() -> new RuntimeException("Greeting not found with id: " + id));
    }

    @Override
    public void deleteGreeting(Long id) {
        if (greetingRepository.existsById(id)) {
            greetingRepository.deleteById(id);
        } else {
            throw new RuntimeException("Greeting not found with id: " + id);
        }
    }
}
