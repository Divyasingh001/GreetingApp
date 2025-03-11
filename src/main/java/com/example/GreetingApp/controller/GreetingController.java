package com.example.GreetingApp.controller;

import com.example.GreetingApp.Exception.ResourceNotFoundException;
import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.service.GreetingServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/greetings")
public class GreetingController {

    private final GreetingServices greetingService;

    public GreetingController(GreetingServices greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/simple")
    public String getSimpleGreeting() {
        return greetingService.getSimpleGreeting();
    }

    @PostMapping("/save")
    public Greeting saveGreeting(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        return greetingService.saveGreeting(firstName, lastName);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Greeting>> getAllGreetings() {
        List<Greeting> greetings = greetingService.getAllGreetings();
        return ResponseEntity.ok(greetings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Greeting> getGreetingById(@PathVariable Long id) {
        return greetingService.getGreetingById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Greeting not found with id " + id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Greeting> updateGreeting(
            @PathVariable Long id, @RequestBody Greeting greetingDetails) {
        Greeting updatedGreeting = greetingService.updateGreeting(id, greetingDetails.getMessage());
        return ResponseEntity.ok(updatedGreeting);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGreeting(@PathVariable Long id) {
        greetingService.deleteGreeting(id);
        return ResponseEntity.ok("Greeting deleted successfully!");
    }
}
