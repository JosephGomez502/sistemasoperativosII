package com.aeropuerto.airport.presentation.controller;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.application.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/admin")
public class AdminController {
  private final AdminService admin;
  public AdminController(AdminService admin) { this.admin = admin; }
  @GetMapping("/dashboard") DashboardResponse dashboard() { return admin.dashboard(); }
  @GetMapping("/airports") List<AirportResponse> airports() { return admin.airports(); }
  @PostMapping("/airports") AirportResponse createAirport(@Valid @RequestBody AirportRequest r) { return admin.saveAirport(r); }
  @PutMapping("/airports/{id}") AirportResponse updateAirport(@PathVariable Long id, @Valid @RequestBody AirportRequest r) { return admin.updateAirport(id, r); }
  @DeleteMapping("/airports/{id}") void deleteAirport(@PathVariable Long id) { admin.deleteAirport(id); }
  @GetMapping("/aircraft") List<AircraftResponse> aircraft() { return admin.aircraft(); }
  @PostMapping("/aircraft") AircraftResponse createAircraft(@Valid @RequestBody AircraftRequest r) { return admin.saveAircraft(r); }
  @PutMapping("/aircraft/{id}") AircraftResponse updateAircraft(@PathVariable Long id, @Valid @RequestBody AircraftRequest r) { return admin.updateAircraft(id, r); }
  @DeleteMapping("/aircraft/{id}") void deleteAircraft(@PathVariable Long id) { admin.deleteAircraft(id); }
  @GetMapping("/flights") List<FlightResponse> flights() { return admin.flights(); }
  @PostMapping("/flights") FlightResponse createFlight(@Valid @RequestBody FlightRequest r) { return admin.saveFlight(r); }
  @PutMapping("/flights/{id}") FlightResponse updateFlight(@PathVariable Long id, @Valid @RequestBody FlightRequest r) { return admin.updateFlight(id, r); }
  @DeleteMapping("/flights/{id}") void deleteFlight(@PathVariable Long id) { admin.deleteFlight(id); }
}
