package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
  public AirportResponse toAirport(Airport a) { return new AirportResponse(a.getId(), a.getName(), a.getCity(), a.getCountry(), a.getIataCode()); }
  public AircraftResponse toAircraft(Aircraft a) { return new AircraftResponse(a.getId(), a.getModel(), a.getCapacity(), a.getAirline()); }
  public SeatResponse toSeat(Seat s) { return new SeatResponse(s.getId(), s.getSeatNumber(), s.getReserved()); }
  public FlightResponse toFlight(Flight f) {
    return new FlightResponse(f.getId(), toAirport(f.getOrigin()), toAirport(f.getDestination()), toAircraft(f.getAircraft()),
        f.getDepartureTime(), f.getArrivalTime(), f.getPrice(), f.getAvailableSeats(), f.getStatus());
  }
  public ReservationResponse toReservation(Reservation r) {
    return new ReservationResponse(r.getId(), r.getCode(), toFlight(r.getFlight()), toSeat(r.getSeat()), r.getStatus(), r.getCreatedAt().toString());
  }
  public ProfileResponse toProfile(User u) { return new ProfileResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getDocumentId(), u.getRole().name()); }
}
