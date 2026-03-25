package com.example.flight_booking_backend.controller;

import com.example.flight_booking_backend.model.City;
import com.example.flight_booking_backend.model.Country;
import com.example.flight_booking_backend.repository.CityRepository;
import com.example.flight_booking_backend.repository.CountryRepository;
import com.example.flight_booking_backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final CountryRepository countryRepo;
    private final CityRepository cityRepo;
    private final JwtUtil jwtUtil;

    public LocationController(CountryRepository countryRepo,
            CityRepository cityRepo,
            JwtUtil jwtUtil) {
        this.countryRepo = countryRepo;
        this.cityRepo = cityRepo;
        this.jwtUtil = jwtUtil;
    }

    // ── Countries (public read) ───────────────────────────────────

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(countryRepo.findAll());
    }

    @PostMapping("/countries")
    public ResponseEntity<?> addCountry(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        String name = body.get("name");
        String code = body.get("code");
        String flag = body.getOrDefault("flag", "");

        if (name == null || name.isBlank())
            return ResponseEntity.badRequest().body("Country name is required");
        if (code == null || code.isBlank() || code.length() != 2)
            return ResponseEntity.badRequest().body("ISO-2 country code is required (e.g. PK)");
        if (countryRepo.existsByNameIgnoreCase(name.trim()))
            return ResponseEntity.badRequest().body("Country already exists");
        if (countryRepo.existsByCode(code.trim().toUpperCase()))
            return ResponseEntity.badRequest().body("Country code already exists");

        Country saved = countryRepo.save(
                new Country(name.trim(), code.trim().toUpperCase(), flag.trim()));
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<?> updateCountry(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        Country country = countryRepo.findById(id).orElse(null);
        if (country == null)
            return ResponseEntity.badRequest().body("Country not found");

        String name = body.get("name");
        String code = body.get("code");
        String flag = body.get("flag");

        if (name != null && !name.isBlank())
            country.setName(name.trim());
        if (code != null && !code.isBlank())
            country.setCode(code.trim().toUpperCase());
        if (flag != null)
            country.setFlag(flag.trim());

        return ResponseEntity.ok(countryRepo.save(country));
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<?> deleteCountry(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        if (!countryRepo.existsById(id))
            return ResponseEntity.badRequest().body("Country not found");

        // Cities are cascade-deleted via orphanRemoval
        countryRepo.deleteById(id);
        return ResponseEntity.ok("Country and its cities deleted");
    }

    // ── Cities (public read) ──────────────────────────────────────

    /** GET /api/locations/cities?countryId=3 OR ?countryCode=PK OR all */
    @GetMapping("/cities")
    public ResponseEntity<List<City>> getCities(
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) String countryCode) {

        if (countryId != null)
            return ResponseEntity.ok(cityRepo.findByCountryId(countryId));
        if (countryCode != null)
            return ResponseEntity.ok(cityRepo.findByCountryCode(countryCode.toUpperCase()));
        return ResponseEntity.ok(cityRepo.findAll());
    }

    @PostMapping("/cities")
    public ResponseEntity<?> addCity(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        String name = body.get("name");
        String countryId = body.get("countryId");

        if (name == null || name.isBlank())
            return ResponseEntity.badRequest().body("City name is required");
        if (countryId == null || countryId.isBlank())
            return ResponseEntity.badRequest().body("countryId is required");

        Long cid = Long.valueOf(countryId);
        Country country = countryRepo.findById(cid).orElse(null);
        if (country == null)
            return ResponseEntity.badRequest().body("Country not found");
        if (cityRepo.existsByNameIgnoreCaseAndCountryId(name.trim(), cid))
            return ResponseEntity.badRequest().body("City already exists in this country");

        City saved = cityRepo.save(new City(name.trim(), country));
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<?> updateCity(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        City city = cityRepo.findById(id).orElse(null);
        if (city == null)
            return ResponseEntity.badRequest().body("City not found");

        String name = body.get("name");
        if (name == null || name.isBlank())
            return ResponseEntity.badRequest().body("City name is required");

        city.setName(name.trim());
        return ResponseEntity.ok(cityRepo.save(city));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<?> deleteCity(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader))
            return ResponseEntity.status(403).body("Access denied: ADMIN role required");

        if (!cityRepo.existsById(id))
            return ResponseEntity.badRequest().body("City not found");

        cityRepo.deleteById(id);
        return ResponseEntity.ok("City deleted");
    }

    // ── Helper ────────────────────────────────────────────────────

    private boolean isAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return false;
        try {
            return "ADMIN".equalsIgnoreCase(jwtUtil.extractRole(authHeader.substring(7)));
        } catch (Exception e) {
            return false;
        }
    }
}