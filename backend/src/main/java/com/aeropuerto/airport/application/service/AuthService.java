package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.model.*;
import com.aeropuerto.airport.domain.repository.UserRepository;
import com.aeropuerto.airport.infrastructure.security.JwtService;
import com.aeropuerto.airport.presentation.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
    this.users = users; this.encoder = encoder; this.jwt = jwt;
  }
  @Transactional
  public AuthResponse register(RegisterRequest r) {
    if (users.existsByEmailIgnoreCase(r.email())) throw new ApiException(HttpStatus.CONFLICT, "El correo ya existe");
    User user = users.save(User.builder().fullName(r.fullName()).email(r.email().toLowerCase())
        .passwordHash(encoder.encode(r.password())).role(Role.CLIENT).phone(r.phone()).documentId(r.documentId()).build());
    return auth(user);
  }
  public AuthResponse login(LoginRequest r) {
    User user = users.findByEmailIgnoreCase(r.email()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
    if (!encoder.matches(r.password(), user.getPasswordHash())) throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
    return auth(user);
  }
  public AuthResponse refresh(RefreshRequest r) {
    String email = jwt.subject(r.refreshToken());
    User user = users.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token invalido"));
    return auth(user);
  }
  private AuthResponse auth(User user) {
    return new AuthResponse(jwt.accessToken(user.getEmail(), user.getRole().name()),
        jwt.refreshToken(user.getEmail(), user.getRole().name()), user.getRole().name(), user.getFullName());
  }
}
