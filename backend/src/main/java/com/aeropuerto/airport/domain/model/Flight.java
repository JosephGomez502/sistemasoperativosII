package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "FLIGHTS", indexes = {
    @Index(name = "IX_FLIGHT_ROUTE_DATE", columnList = "ORIGIN_ID,DESTINATION_ID,DEPARTURE_TIME"),
    @Index(name = "IX_FLIGHT_STATUS", columnList = "STATUS")
})
public class Flight {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight_seq")
  @SequenceGenerator(name = "flight_seq", sequenceName = "SEQ_FLIGHTS", allocationSize = 1)
  private Long id;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "ORIGIN_ID") private Airport origin;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "DESTINATION_ID") private Airport destination;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "AIRCRAFT_ID") private Aircraft aircraft;
  @Column(name = "DEPARTURE_TIME", nullable = false) private OffsetDateTime departureTime;
  @Column(name = "ARRIVAL_TIME", nullable = false) private OffsetDateTime arrivalTime;
  @Column(name = "PRICE", nullable = false, precision = 12, scale = 2) private BigDecimal price;
  @Column(name = "AVAILABLE_SEATS", nullable = false) private Integer availableSeats;
  @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private FlightStatus status;
}
