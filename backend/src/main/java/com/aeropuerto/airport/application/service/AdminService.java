package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.model.*;
import com.aeropuerto.airport.domain.repository.*;
import com.aeropuerto.airport.presentation.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {
  private final AirportRepository airports; private final AircraftRepository aircraft; private final FlightRepository flights;
  private final SeatRepository seats; private final UserRepository users; private final ReservationRepository reservations;
  private final PaymentRepository payments; private final EntityMapper mapper;
  public AdminService(AirportRepository airports, AircraftRepository aircraft, FlightRepository flights, SeatRepository seats,
      UserRepository users, ReservationRepository reservations, PaymentRepository payments, EntityMapper mapper) {
    this.airports = airports; this.aircraft = aircraft; this.flights = flights; this.seats = seats; this.users = users;
    this.reservations = reservations; this.payments = payments; this.mapper = mapper;
  }
  public List<AirportResponse> airports() { return airports.findAll().stream().map(mapper::toAirport).toList(); }
  @Transactional public AirportResponse saveAirport(AirportRequest r) {
    Airport a = airports.save(Airport.builder().name(r.name()).city(r.city()).country(r.country()).iataCode(r.iataCode().toUpperCase()).build());
    return mapper.toAirport(a);
  }
  @Transactional public AirportResponse updateAirport(Long id, AirportRequest r) {
    Airport a = airports.findById(id).orElseThrow(() -> notFound("Aeropuerto no encontrado"));
    a.setName(r.name()); a.setCity(r.city()); a.setCountry(r.country()); a.setIataCode(r.iataCode().toUpperCase());
    return mapper.toAirport(a);
  }
  @Transactional public void deleteAirport(Long id) { airports.deleteById(id); }
  public List<AircraftResponse> aircraft() { return aircraft.findAll().stream().map(mapper::toAircraft).toList(); }
  @Transactional public AircraftResponse saveAircraft(AircraftRequest r) {
    return mapper.toAircraft(aircraft.save(Aircraft.builder().model(r.model()).capacity(r.capacity()).airline(r.airline()).build()));
  }
  @Transactional public AircraftResponse updateAircraft(Long id, AircraftRequest r) {
    Aircraft a = aircraft.findById(id).orElseThrow(() -> notFound("Avion no encontrado"));
    a.setModel(r.model()); a.setCapacity(r.capacity()); a.setAirline(r.airline());
    return mapper.toAircraft(a);
  }
  @Transactional public void deleteAircraft(Long id) { aircraft.deleteById(id); }
  public List<FlightResponse> flights() { return flights.findAll().stream().map(mapper::toFlight).toList(); }
  @Transactional public FlightResponse saveFlight(FlightRequest r) {
    Aircraft plane = aircraft.findById(r.aircraftId()).orElseThrow(() -> notFound("Avion no encontrado"));
    Flight f = flights.save(Flight.builder()
        .origin(airports.findById(r.originId()).orElseThrow(() -> notFound("Origen no encontrado")))
        .destination(airports.findById(r.destinationId()).orElseThrow(() -> notFound("Destino no encontrado")))
        .aircraft(plane).departureTime(r.departureTime()).arrivalTime(r.arrivalTime()).price(r.price())
        .availableSeats(plane.getCapacity()).status(r.status()).build());
    createSeats(f, plane.getCapacity());
    return mapper.toFlight(f);
  }
  @Transactional public FlightResponse updateFlight(Long id, FlightRequest r) {
    Flight f = flights.findById(id).orElseThrow(() -> notFound("Vuelo no encontrado"));
    f.setOrigin(airports.findById(r.originId()).orElseThrow(() -> notFound("Origen no encontrado")));
    f.setDestination(airports.findById(r.destinationId()).orElseThrow(() -> notFound("Destino no encontrado")));
    f.setAircraft(aircraft.findById(r.aircraftId()).orElseThrow(() -> notFound("Avion no encontrado")));
    f.setDepartureTime(r.departureTime()); f.setArrivalTime(r.arrivalTime()); f.setPrice(r.price()); f.setStatus(r.status());
    return mapper.toFlight(f);
  }
  @Transactional public void deleteFlight(Long id) { flights.deleteById(id); }
  public DashboardResponse dashboard() {
    return new DashboardResponse(payments.sumByStatus(PaymentStatus.APPROVED),
        flights.search(null, null, java.time.OffsetDateTime.now().minusDays(1), FlightStatus.SCHEDULED).size(),
        users.count(), reservations.countByStatus(ReservationStatus.CONFIRMED), airports.count(), aircraft.count());
  }
  public List<CustomerResponse> customers() {
    return users.findAll().stream()
        .map(u -> new CustomerResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getDocumentId(),
            u.getRole().name(), u.getCreatedAt().toString(), reservations.countByUserId(u.getId())))
        .toList();
  }
  public List<ReservationResponse> reservations() {
    return reservations.findAllByOrderByCreatedAtDesc().stream().map(mapper::toReservation).toList();
  }
  public List<PaymentResponse> payments() {
    return payments.findAllByOrderByCreatedAtDesc().stream()
        .map(p -> new PaymentResponse(p.getId(), p.getReservation().getCode(), p.getAmount(), p.getStatus().name(),
            p.getAuthorizationCode(), p.getCardLast4(), p.getCreatedAt().toString()))
        .toList();
  }
  private void createSeats(Flight f, int capacity) {
    for (int i = 1; i <= capacity; i++) {
      String seat = ((char) ('A' + ((i - 1) % 6))) + String.valueOf(((i - 1) / 6) + 1);
      seats.save(Seat.builder().flight(f).seatNumber(seat).reserved(false).build());
    }
  }
  private ApiException notFound(String m) { return new ApiException(HttpStatus.NOT_FOUND, m); }
}
