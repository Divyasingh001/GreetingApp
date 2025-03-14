package com.example.GreetingApp.model;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Greeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String message;

    public Greeting() {}

    public Greeting(String message) {
        this.message = message;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
