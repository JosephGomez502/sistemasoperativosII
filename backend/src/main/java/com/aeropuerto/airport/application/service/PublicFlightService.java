package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.model.FlightStatus;
import com.aeropuerto.airport.domain.repository.*;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PublicFlightService {
  private final FlightRepository flights; private final SeatRepository seats; private final EntityMapper mapper;
  public PublicFlightService(FlightRepository flights, SeatRepository seats, EntityMapper mapper) {
    this.flights = flights; this.seats = seats; this.mapper = mapper;
  }
  public List<FlightResponse> search(String origin, String destination) {
    return flights.search(blank(origin), blank(destination), OffsetDateTime.now().minusHours(1), FlightStatus.SCHEDULED)
        .stream().map(mapper::toFlight).toList();
  }
  public List<SeatResponse> seats(Long flightId) { return seats.findByFlightIdOrderBySeatNumber(flightId).stream().map(mapper::toSeat).toList(); }
  private String blank(String v) { return v == null || v.isBlank() ? null : v; }
}
