package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {
  Optional<Airport> findByIataCodeIgnoreCase(String iataCode);
}
