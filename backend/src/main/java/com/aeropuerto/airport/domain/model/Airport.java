package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "AIRPORTS", indexes = @Index(name = "IX_AIRPORT_IATA", columnList = "IATA_CODE", unique = true))
public class Airport {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "airport_seq")
  @SequenceGenerator(name = "airport_seq", sequenceName = "SEQ_AIRPORTS", allocationSize = 1)
  private Long id;
  @Column(name = "NAME", nullable = false, length = 180) private String name;
  @Column(name = "CITY", nullable = false, length = 120) private String city;
  @Column(name = "COUNTRY", nullable = false, length = 120) private String country;
  @Column(name = "IATA_CODE", nullable = false, length = 3, unique = true) private String iataCode;
}
