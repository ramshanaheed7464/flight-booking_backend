package com.example.flight_booking_backend.repository;

import com.example.flight_booking_backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByCode(String code);

    Optional<Country> findByCode(String code);
}