package com.example.flight_booking_backend.controller;

import com.example.flight_booking_backend.model.Flight;
import com.example.flight_booking_backend.repository.FlightRepository;
import com.example.flight_booking_backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;
    private final JwtUtil jwtUtil;

    public FlightController(FlightRepository flightRepository, JwtUtil jwtUtil) {
        this.flightRepository = flightRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFlight(
            @RequestBody Flight flight,
            @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied");
        flightRepository.save(flight);
        return ResponseEntity.ok("Flight added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(
            @PathVariable Long id,
            @RequestBody Flight updated,
            @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied");

        Flight flight = flightRepository.findById(id).orElse(null);
        if (flight == null)
            return ResponseEntity.badRequest().body("Flight not found");

        flight.setFlightNumber(updated.getFlightNumber());
        flight.setSource(updated.getSource());
        flight.setDestination(updated.getDestination());
        flight.setDepartureTime(updated.getDepartureTime());
        flight.setArrivalTime(updated.getArrivalTime());
        flight.setSeatsAvailable(updated.getSeatsAvailable());
        flight.setPrice(updated.getPrice());
        flightRepository.save(flight);
        return ResponseEntity.ok("Flight updated successfully");
    }

    // DELETE /api/flights/{id} — admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlight(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied");

        if (!flightRepository.existsById(id))
            return ResponseEntity.badRequest().body("Flight not found");

        flightRepository.deleteById(id);
        return ResponseEntity.ok("Flight deleted successfully");
    }

    private boolean isAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return false;
        try {
            String role = jwtUtil.extractRole(authHeader.substring(7));
            return "ADMIN".equalsIgnoreCase(role);
        } catch (Exception e) {
            return false;
        }
    }
}