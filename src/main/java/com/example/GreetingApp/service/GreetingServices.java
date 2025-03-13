package com.example.GreetingApp.service;
import com.example.GreetingApp.Exception.UserException;
import com.example.GreetingApp.Interface.IGreetingService;
import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.repository.GreetingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GreetingServices implements IGreetingService {

    @Autowired
    private GreetingRepository greetingRepository;

    @Override
    public String getSimpleGreeting() {
        log.info("Fetching simple greeting...");
        return "Hello World!";
    }

    @Override
    public Greeting addGreeting(String message) {
        try {
            log.info("Adding new greeting with message: {}", message);
            Greeting greeting = new Greeting(message != null ? message : "Hello World!");
            Greeting savedGreeting = greetingRepository.save(greeting);
            log.info("Greeting added successfully with ID: {}", savedGreeting.getId());
            return savedGreeting;
        } catch (Exception e) {
            log.error("Failed to add greeting, Error: {}", e.getMessage());
            throw new UserException("Error while adding greeting: " + e.getMessage());
        }
    }

    @Override
    public Greeting saveGreeting(String firstName, String lastName) {
        try {
            log.info("Saving greeting for firstName: {}, lastName: {}", firstName, lastName);

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
            Greeting savedGreeting = greetingRepository.save(greeting);

            log.info("Greeting saved successfully with ID: {}", savedGreeting.getId());
            return savedGreeting;
        } catch (Exception e) {
            log.error("Failed to save greeting, Error: {}", e.getMessage());
            throw new UserException("Error while saving greeting: " + e.getMessage());
        }
    }

    @Override
    public Optional<Greeting> getGreetingById(Long id) {
        try {
            log.info("Fetching greeting with ID: {}", id);
            Optional<Greeting> greeting = greetingRepository.findById(id);
            if (greeting.isPresent()) {
                log.info("Greeting found: {}", greeting.get().getMessage());
                return greeting;
            } else {
                log.warn("Greeting not found with ID: {}", id);
                throw new UserException("Greeting not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Failed to fetch greeting, Error: {}", e.getMessage());
            throw new UserException("Error while fetching greeting: " + e.getMessage());
        }
    }

    @Override
    public List<Greeting> getAllGreetings() {
        try {
            log.info("Fetching all greetings...");
            List<Greeting> greetings = greetingRepository.findAll();
            log.info("Total greetings found: {}", greetings.size());
            return greetings;
        } catch (Exception e) {
            log.error("Failed to fetch greetings, Error: {}", e.getMessage());
            throw new UserException("Error while fetching all greetings: " + e.getMessage());
        }
    }

    @Override
    public Greeting updateGreeting(Long id, String newMessage) {
        try {
            log.info("Updating greeting with ID: {} to new message: {}", id, newMessage);
            return greetingRepository.findById(id)
                    .map(greeting -> {
                        greeting.setMessage(newMessage);
                        Greeting updatedGreeting = greetingRepository.save(greeting);
                        log.info("Greeting updated successfully with ID: {}", updatedGreeting.getId());
                        return updatedGreeting;
                    })
                    .orElseThrow(() -> {
                        log.error("Failed to update. Greeting not found with ID: {}", id);
                        return new UserException("Greeting not found with ID: " + id);
                    });
        } catch (Exception e) {
            log.error("Failed to update greeting, Error: {}", e.getMessage());
            throw new UserException("Error while updating greeting: " + e.getMessage());
        }
    }

    @Override
    public void deleteGreeting(Long id) {
        try {
            log.info("Attempting to delete greeting with ID: {}", id);
            if (greetingRepository.existsById(id)) {
                greetingRepository.deleteById(id);
                log.info("Greeting deleted successfully with ID: {}", id);
            } else {
                log.error("Failed to delete. Greeting not found with ID: {}", id);
                throw new UserException("Greeting not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Failed to delete greeting, Error: {}", e.getMessage());
            throw new UserException("Error while deleting greeting: " + e.getMessage());
        }
    }
}
