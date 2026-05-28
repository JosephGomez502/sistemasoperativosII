package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "PAYMENTS")
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
  @SequenceGenerator(name = "payment_seq", sequenceName = "SEQ_PAYMENTS", allocationSize = 1)
  private Long id;
  @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "RESERVATION_ID") private Reservation reservation;
  @Column(name = "AMOUNT", nullable = false, precision = 12, scale = 2) private BigDecimal amount;
  @Enumerated(EnumType.STRING) @Column(name = "STATUS", nullable = false, length = 20) private PaymentStatus status;
  @Column(name = "AUTHORIZATION_CODE", nullable = false, length = 40) private String authorizationCode;
  @Column(name = "CARD_LAST4", nullable = false, length = 4) private String cardLast4;
  @Column(name = "CREATED_AT", nullable = false) private Instant createdAt;
  @PrePersist void prePersist() { if (createdAt == null) createdAt = Instant.now(); }
}
