package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "AIRCRAFT")
public class Aircraft {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aircraft_seq")
  @SequenceGenerator(name = "aircraft_seq", sequenceName = "SEQ_AIRCRAFT", allocationSize = 1)
  private Long id;
  @Column(name = "MODEL", nullable = false, length = 120) private String model;
  @Column(name = "CAPACITY", nullable = false) private Integer capacity;
  @Column(name = "AIRLINE", nullable = false, length = 140) private String airline;
}
