package com.aeropuerto.airport.presentation.controller;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.application.service.PublicFlightService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/public")
public class PublicController {
  private final PublicFlightService service;
  public PublicController(PublicFlightService service) { this.service = service; }
  @GetMapping("/flights") List<FlightResponse> flights(@RequestParam(required = false) String origin, @RequestParam(required = false) String destination) {
    return service.search(origin, destination);
  }
  @GetMapping("/flights/{flightId}/seats") List<SeatResponse> seats(@PathVariable Long flightId) { return service.seats(flightId); }
}
