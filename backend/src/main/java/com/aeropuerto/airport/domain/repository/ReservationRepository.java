package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  Optional<Reservation> findByCode(String code);
  List<Reservation> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String email);
  long countByStatus(com.aeropuerto.airport.domain.model.ReservationStatus status);
}
