package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "RESERVATIONS", indexes = @Index(name = "IX_RESERVATION_CODE", columnList = "CODE", unique = true))
public class Reservation {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
  @SequenceGenerator(name = "reservation_seq", sequenceName = "SEQ_RESERVATIONS", allocationSize = 1)
  private Long id;
  @Column(name = "CODE", nullable = false, unique = true, length = 24) private String code;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "USER_ID") private User user;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "FLIGHT_ID") private Flight flight;
  @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "SEAT_ID") private Seat seat;
  @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 30) private ReservationStatus status;
  @Column(name = "CREATED_AT", nullable = false) private Instant createdAt;
  @PrePersist void prePersist() { if (createdAt == null) createdAt = Instant.now(); }
}
