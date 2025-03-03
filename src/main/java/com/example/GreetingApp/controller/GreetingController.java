package com.example.GreetingApp.controller;
import com.example.GreetingApp.Exception.ResourceNotFoundException;
import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.repository.GreetingRepository;
import com.example.GreetingApp.service.GreetingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/greetings")
public class GreetingController {

    @Autowired
    private GreetingRepository greetingRepository;
    @Autowired
    private GreetingServices greetingService;
    @GetMapping("/simple")
    public String getSimpleGreeting() {
        return greetingService.getSimpleGreeting();
    }
    @GetMapping("/message")
    public String getPersonalizedGreeting(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        return greetingService.getGreeting(firstName, lastName);
    }
    @GetMapping("/findall")
    public ResponseEntity<?> getAllGreetings() {
        try {
            List<Greeting> greetings = greetingRepository.findAll();
            return ResponseEntity.ok(greetings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching greetings: " + e.getMessage());
        }
    }
    @PostMapping("/add")
    public Greeting createGreeting(@RequestBody Greeting greeting) {
        return greetingRepository.save(greeting);
    }
    @PutMapping("/{id}")
    public Greeting updateGreeting(@PathVariable Long id, @RequestBody Greeting greetingDetails) {
        Greeting greeting = greetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Greeting not found with id " + id));
        greeting.setMessage(greetingDetails.getMessage());
        return greetingRepository.save(greeting);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGreeting(@PathVariable Long id) {
        Greeting greeting = greetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Greeting not found with id " + id));
        greetingRepository.delete(greeting);
        return ResponseEntity.ok().build();
    }

}