package com.example.flight_booking_backend;

import com.example.flight_booking_backend.model.Flight;
import com.example.flight_booking_backend.model.User;
import com.example.flight_booking_backend.repository.FlightRepository;
import com.example.flight_booking_backend.repository.UserRepository;
import com.example.flight_booking_backend.repository.BookingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(FlightRepository flightRepository, UserRepository userRepository,
            BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("ali@example.com").isEmpty()) {
            User u = new User();
            u.setName("Ali Khan");
            u.setEmail("ali@example.com");
            u.setPassword(passwordEncoder.encode("123456"));
            u.setRole("USER");
            userRepository.save(u);
        }
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User a = new User();
            a.setName("Admin");
            a.setEmail("admin@example.com");
            a.setPassword(passwordEncoder.encode("admin123"));
            a.setRole("ADMIN");
            userRepository.save(a);
        }

        if (flightRepository.count() == 0) {
            flightRepository.save(
                    new Flight("PK101", "Karachi", "Lahore", dt(2026, 3, 20, 8, 0), dt(2026, 3, 20, 10, 0), 100, 85));
            flightRepository.save(
                    new Flight("PK102", "Lahore", "Karachi", dt(2026, 3, 20, 14, 0), dt(2026, 3, 20, 16, 0), 80, 85));
            flightRepository.save(
                    new Flight("PK201", "Islamabad", "Karachi", dt(2026, 3, 21, 9, 0), dt(2026, 3, 21, 11, 0), 90, 95));
            flightRepository.save(new Flight("PK202", "Karachi", "Islamabad", dt(2026, 3, 21, 15, 0),
                    dt(2026, 3, 21, 17, 0), 70, 95));
            flightRepository.save(
                    new Flight("PK301", "Lahore", "Islamabad", dt(2026, 3, 22, 7, 0), dt(2026, 3, 22, 8, 30), 120, 55));
            flightRepository.save(new Flight("PK302", "Islamabad", "Lahore", dt(2026, 3, 22, 12, 0),
                    dt(2026, 3, 22, 13, 30), 110, 55));
            flightRepository.save(
                    new Flight("PK401", "Karachi", "Peshawar", dt(2026, 3, 23, 6, 0), dt(2026, 3, 23, 8, 30), 60, 120));
            flightRepository.save(new Flight("PK402", "Peshawar", "Karachi", dt(2026, 3, 23, 16, 0),
                    dt(2026, 3, 23, 18, 30), 55, 120));
            flightRepository.save(
                    new Flight("PK501", "Lahore", "Quetta", dt(2026, 3, 24, 10, 0), dt(2026, 3, 24, 12, 30), 75, 110));
            flightRepository.save(
                    new Flight("PK502", "Quetta", "Lahore", dt(2026, 3, 24, 18, 0), dt(2026, 3, 24, 20, 30), 70, 110));
            System.out.println("10 flights seeded.");
        }
    }

    private LocalDateTime dt(int y, int mo, int d, int h, int min) {
        return LocalDateTime.of(y, mo, d, h, min);
    }
}