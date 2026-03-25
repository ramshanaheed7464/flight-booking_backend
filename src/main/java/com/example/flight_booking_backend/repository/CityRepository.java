package com.example.flight_booking_backend.repository;

import com.example.flight_booking_backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByCountryId(Long countryId);

    List<City> findByCountryCode(String countryCode);

    boolean existsByNameIgnoreCaseAndCountryId(String name, Long countryId);
}