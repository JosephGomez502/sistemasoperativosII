package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Flight;
import com.aeropuerto.airport.domain.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
  @Query("""
      select f from Flight f
      join fetch f.origin o join fetch f.destination d join fetch f.aircraft a
      where (:origin is null or upper(o.iataCode) = upper(:origin))
        and (:destination is null or upper(d.iataCode) = upper(:destination))
        and f.departureTime >= :from
        and f.status = :status
      order by f.departureTime
      """)
  List<Flight> search(@Param("origin") String origin, @Param("destination") String destination,
                      @Param("from") OffsetDateTime from, @Param("status") FlightStatus status);
}
