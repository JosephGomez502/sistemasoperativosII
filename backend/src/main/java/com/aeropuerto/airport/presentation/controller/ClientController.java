package com.aeropuerto.airport.presentation.controller;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.application.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/client")
public class ClientController {
  private final BookingService bookings; private final ProfileService profiles;
  public ClientController(BookingService bookings, ProfileService profiles) { this.bookings = bookings; this.profiles = profiles; }
  @GetMapping("/profile") ProfileResponse me(Authentication auth) { return profiles.me(auth.getName()); }
  @PutMapping("/profile") ProfileResponse update(Authentication auth, @Valid @RequestBody ProfileRequest r) { return profiles.update(auth.getName(), r); }
  @PostMapping("/checkout") ReservationResponse checkout(Authentication auth, @Valid @RequestBody CheckoutRequest r) { return bookings.checkout(auth.getName(), r); }
  @GetMapping("/reservations") List<ReservationResponse> reservations(Authentication auth) { return bookings.mine(auth.getName()); }
  @GetMapping("/reservations/{code}/ticket.pdf")
  ResponseEntity<byte[]> ticket(Authentication auth, @PathVariable String code) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + code + ".pdf")
        .contentType(MediaType.APPLICATION_PDF).body(bookings.ticket(auth.getName(), code));
  }
  @PostMapping("/reservations/{code}/email")
  ReservationResponse resendEmail(Authentication auth, @PathVariable String code) { return bookings.resendTicket(auth.getName(), code); }
}
