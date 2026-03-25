package com.example.flight_booking_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /** ISO-3166-1 alpha-2 code, e.g. "PK", "AE" */
    @Column(nullable = false, unique = true, length = 2)
    private String code;

    /** Unicode flag emoji stored as-is, e.g. "🇵🇰" */
    @Column
    private String flag;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<City> cities;

    public Country() {
    }

    public Country(String name, String code, String flag) {
        this.name = name;
        this.code = code;
        this.flag = flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}