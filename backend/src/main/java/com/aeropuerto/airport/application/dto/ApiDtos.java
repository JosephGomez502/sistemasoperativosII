package com.aeropuerto.airport.application.dto;

import com.aeropuerto.airport.domain.model.FlightStatus;
import com.aeropuerto.airport.domain.model.ReservationStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class ApiDtos {
  private ApiDtos() {}
  public record RegisterRequest(@NotBlank String fullName, @Email @NotBlank String email,
                                @Size(min = 8) String password, String phone, String documentId) {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record RefreshRequest(@NotBlank String refreshToken) {}
  public record AuthResponse(String accessToken, String refreshToken, String role, String fullName) {}
  public record AirportRequest(@NotBlank String name, @NotBlank String city, @NotBlank String country,
                               @Pattern(regexp = "^[A-Za-z]{3}$") String iataCode) {}
  public record AirportResponse(Long id, String name, String city, String country, String iataCode) {}
  public record AircraftRequest(@NotBlank String model, @Min(1) Integer capacity, @NotBlank String airline) {}
  public record AircraftResponse(Long id, String model, Integer capacity, String airline) {}
  public record FlightRequest(@NotNull Long originId, @NotNull Long destinationId, @NotNull Long aircraftId,
                              @Future OffsetDateTime departureTime, @Future OffsetDateTime arrivalTime,
                              @DecimalMin("0.01") BigDecimal price, @NotNull FlightStatus status) {}
  public record FlightResponse(Long id, AirportResponse origin, AirportResponse destination, AircraftResponse aircraft,
                               OffsetDateTime departureTime, OffsetDateTime arrivalTime, BigDecimal price,
                               Integer availableSeats, FlightStatus status) {}
  public record SeatResponse(Long id, String seatNumber, Boolean reserved) {}
  public record CheckoutRequest(@NotNull Long flightId, @NotBlank String seatNumber, @NotBlank String cardNumber,
                                @NotBlank String cardHolder, @NotBlank String expiry, @NotBlank String cvv) {}
  public record ReservationResponse(Long id, String code, FlightResponse flight, SeatResponse seat,
                                    ReservationStatus status, String createdAt) {}
  public record ProfileRequest(@NotBlank String fullName, String phone, String documentId) {}
  public record ProfileResponse(Long id, String fullName, String email, String phone, String documentId, String role) {}
  public record DashboardResponse(BigDecimal sales, long activeFlights, long users, long confirmedReservations) {}
}
