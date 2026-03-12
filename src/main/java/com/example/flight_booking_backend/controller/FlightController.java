package com.example.flight_booking_backend.controller;

import com.example.flight_booking_backend.model.Flight;
import com.example.flight_booking_backend.repository.FlightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    // Anyone can view flights
    @GetMapping
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    // Only ADMIN can add flights
    @PostMapping("/add")
    public ResponseEntity<?> addFlight(@RequestHeader("role") String role, @RequestBody Flight flight) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        flightRepository.save(flight);
        return ResponseEntity.ok("Flight added successfully");
    }
}