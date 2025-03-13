package com.example.GreetingApp;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class GreetingAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(GreetingAppApplication.class, args);
	}
}
