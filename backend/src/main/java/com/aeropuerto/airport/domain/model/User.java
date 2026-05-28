package com.aeropuerto.airport.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "APP_USERS", indexes = @Index(name = "IX_USERS_EMAIL", columnList = "EMAIL", unique = true))
public class User {
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "SEQ_USERS", allocationSize = 1)
  private Long id;
  @Column(name = "FULL_NAME", nullable = false, length = 160)
  private String fullName;
  @Column(name = "EMAIL", nullable = false, unique = true, length = 180)
  private String email;
  @Column(name = "PASSWORD_HASH", nullable = false, length = 120)
  private String passwordHash;
  @Enumerated(EnumType.STRING) @Column(name = "ROLE", nullable = false, length = 20)
  private Role role;
  @Column(name = "PHONE", length = 40)
  private String phone;
  @Column(name = "DOCUMENT_ID", length = 60)
  private String documentId;
  @Column(name = "CREATED_AT", nullable = false)
  private Instant createdAt;
  @PrePersist void prePersist() { if (createdAt == null) createdAt = Instant.now(); }
}
