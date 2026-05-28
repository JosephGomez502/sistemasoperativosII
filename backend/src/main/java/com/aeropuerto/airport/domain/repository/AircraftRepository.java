package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {}
