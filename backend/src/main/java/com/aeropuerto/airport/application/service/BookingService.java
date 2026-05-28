package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.model.*;
import com.aeropuerto.airport.domain.repository.*;
import com.aeropuerto.airport.infrastructure.pdf.TicketPdfService;
import com.aeropuerto.airport.presentation.error.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
  private final UserRepository users; private final FlightRepository flights; private final SeatRepository seats;
  private final ReservationRepository reservations; private final PaymentRepository payments; private final EntityMapper mapper;
  private final TicketPdfService pdf; private final double approvalRate; private final SecureRandom random = new SecureRandom();
  public BookingService(UserRepository users, FlightRepository flights, SeatRepository seats, ReservationRepository reservations,
      PaymentRepository payments, EntityMapper mapper, TicketPdfService pdf, @Value("${app.payment.approval-rate}") double approvalRate) {
    this.users = users; this.flights = flights; this.seats = seats; this.reservations = reservations; this.payments = payments;
    this.mapper = mapper; this.pdf = pdf; this.approvalRate = approvalRate;
  }
  @Transactional
  public ReservationResponse checkout(String email, CheckoutRequest r) {
    User user = users.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario invalido"));
    Flight flight = flights.findById(r.flightId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vuelo no encontrado"));
    Seat seat = seats.findByFlightIdAndSeatNumber(r.flightId(), r.seatNumber()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Asiento no encontrado"));
    if (Boolean.TRUE.equals(seat.getReserved()) || flight.getAvailableSeats() <= 0) throw new ApiException(HttpStatus.CONFLICT, "Asiento no disponible");
    Reservation reservation = reservations.save(Reservation.builder().code(code()).user(user).flight(flight).seat(seat).status(ReservationStatus.PENDING_PAYMENT).build());
    boolean approved = random.nextDouble() <= approvalRate;
    payments.save(Payment.builder().reservation(reservation).amount(flight.getPrice())
        .status(approved ? PaymentStatus.APPROVED : PaymentStatus.REJECTED).authorizationCode(UUID.randomUUID().toString())
        .cardLast4(last4(r.cardNumber())).build());
    if (!approved) {
      reservation.setStatus(ReservationStatus.REJECTED);
      throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "Pago rechazado por el simulador");
    }
    seat.setReserved(true);
    flight.setAvailableSeats(flight.getAvailableSeats() - 1);
    reservation.setStatus(ReservationStatus.CONFIRMED);
    return mapper.toReservation(reservation);
  }
  @Transactional(readOnly = true)
  public List<ReservationResponse> mine(String email) {
    return reservations.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(email).stream().map(mapper::toReservation).toList();
  }
  @Transactional(readOnly = true)
  public byte[] ticket(String email, String code) {
    Reservation r = reservations.findByCode(code).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    if (!r.getUser().getEmail().equalsIgnoreCase(email) && !r.getUser().getRole().equals(Role.ADMIN)) throw new ApiException(HttpStatus.FORBIDDEN, "Sin permisos");
    if (r.getStatus() != ReservationStatus.CONFIRMED) throw new ApiException(HttpStatus.CONFLICT, "Reserva no confirmada");
    return pdf.render(r);
  }
  private String code() { return "AP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
  private String last4(String card) { String digits = card.replaceAll("\\D", ""); return digits.substring(Math.max(0, digits.length() - 4)); }
}
