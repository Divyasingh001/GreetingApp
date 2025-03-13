package com.example.GreetingApp.service;

import static org.junit.jupiter.api.Assertions.*;
import com.example.GreetingApp.Exception.UserException;
import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.repository.GreetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GreetingServicesTest {

    @Mock
    private GreetingRepository greetingRepository;

    @InjectMocks
    private GreetingServices greetingServices;

    private Greeting testGreeting;

    @BeforeEach
    void setUp() {
        testGreeting = new Greeting("Hello, Test!");
        testGreeting.setId(1L);
    }

    // Test for getSimpleGreeting()
    @Test
    void testGetSimpleGreeting() {
        assertEquals("Hello World!", greetingServices.getSimpleGreeting());
    }

    // Test for addGreeting()
    @Test
    void testAddGreeting() {
        when(greetingRepository.save(Mockito.any(Greeting.class))).thenReturn(testGreeting);

        Greeting savedGreeting = greetingServices.addGreeting("Hello, Test!");

        assertNotNull(savedGreeting);
        assertEquals("Hello, Test!", savedGreeting.getMessage());
        assertTrue(savedGreeting.getId() > 0);

        verify(greetingRepository, times(1)).save(Mockito.any(Greeting.class));
    }


    // Test for getGreetingById()
    @Test
    void testGetGreetingById() {
        when(greetingRepository.findById(1L)).thenReturn(Optional.of(testGreeting));

        Optional<Greeting> retrievedGreeting = greetingServices.getGreetingById(1L);

        assertTrue(retrievedGreeting.isPresent());
        assertEquals("Hello, Test!", retrievedGreeting.get().getMessage()); // Fixed assertion

        verify(greetingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetGreetingById_NotFound() {
        when(greetingRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserException.class, () -> greetingServices.getGreetingById(2L));
        assertEquals("Error while fetching greeting: Greeting not found with ID: 2", exception.getMessage()); // Ensure service message matches

        verify(greetingRepository, times(1)).findById(2L);
    }

    // Test for getAllGreetings()
    @Test
    void testGetAllGreetings() {
        List<Greeting> mockGreetings = Arrays.asList(testGreeting, new Greeting("Hello, World!"));
        when(greetingRepository.findAll()).thenReturn(mockGreetings);

        List<Greeting> greetings = greetingServices.getAllGreetings();

        assertNotNull(greetings);
        assertFalse(greetings.isEmpty());
        assertEquals(2, greetings.size());

        verify(greetingRepository, times(1)).findAll();
    }

    // Test for updateGreeting()
    @Test
    void testUpdateGreeting() {
        when(greetingRepository.findById(1L)).thenReturn(Optional.of(testGreeting));
        when(greetingRepository.save(Mockito.any(Greeting.class))).thenReturn(testGreeting);

        Greeting updatedGreeting = greetingServices.updateGreeting(1L, "Updated Greeting");

        assertNotNull(updatedGreeting);
        assertEquals("Updated Greeting", updatedGreeting.getMessage());

        verify(greetingRepository, times(1)).save(Mockito.any(Greeting.class));
    }

    @Test
    void testUpdateGreeting_NotFound() {
        when(greetingRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserException.class, () -> greetingServices.updateGreeting(2L, "New Message"));
        assertEquals("Error while updating greeting: Greeting not found with ID: 2", exception.getMessage()); // Ensure this matches service

        verify(greetingRepository, times(1)).findById(2L);
    }

    // Test for deleteGreeting()
    @Test
    void testDeleteGreeting() {
        when(greetingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(greetingRepository).deleteById(1L);

        greetingServices.deleteGreeting(1L);

        verify(greetingRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteGreeting_NotFound() {
        when(greetingRepository.existsById(2L)).thenReturn(false);

        Exception exception = assertThrows(UserException.class, () -> greetingServices.deleteGreeting(2L));
        assertEquals("Error while deleting greeting: Greeting not found with ID: 2", exception.getMessage()); // Ensure this matches service

        verify(greetingRepository, times(1)).existsById(2L);
    }
}
