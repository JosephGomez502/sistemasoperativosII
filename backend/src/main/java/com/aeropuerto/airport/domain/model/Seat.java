package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "SEATS", uniqueConstraints = @UniqueConstraint(name = "UK_SEAT_FLIGHT_NUMBER", columnNames = {"FLIGHT_ID", "SEAT_NUMBER"}))
public class Seat {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_seq")
  @SequenceGenerator(name = "seat_seq", sequenceName = "SEQ_SEATS", allocationSize = 1)
  private Long id;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "FLIGHT_ID") private Flight flight;
  @Column(name = "SEAT_NUMBER", nullable = false, length = 8) private String seatNumber;
  @Column(name = "RESERVED", nullable = false) private Boolean reserved;
}
