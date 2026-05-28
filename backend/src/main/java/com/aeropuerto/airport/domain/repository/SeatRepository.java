package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
  List<Seat> findByFlightIdOrderBySeatNumber(Long flightId);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Seat> findByFlightIdAndSeatNumber(Long flightId, String seatNumber);
}
