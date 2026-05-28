package com.aeropuerto.airport.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key;
  private final long accessMinutes;
  private final long refreshDays;
  public JwtService(@Value("${app.security.jwt-secret}") String secret,
                    @Value("${app.security.access-minutes}") long accessMinutes,
                    @Value("${app.security.refresh-days}") long refreshDays) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessMinutes = accessMinutes;
    this.refreshDays = refreshDays;
  }
  public String accessToken(String email, String role) { return token(email, role, accessMinutes * 60); }
  public String refreshToken(String email, String role) { return token(email, role, refreshDays * 24 * 3600); }
  public String subject(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
  public String role(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class); }
  private String token(String email, String role, long seconds) {
    Instant now = Instant.now();
    return Jwts.builder().subject(email).claim("role", role).issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(seconds))).signWith(key).compact();
  }
}
