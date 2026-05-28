package com.aeropuerto.airport.presentation.controller;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final AuthService auth;
  public AuthController(AuthService auth) { this.auth = auth; }
  @PostMapping("/register") AuthResponse register(@Valid @RequestBody RegisterRequest r) { return auth.register(r); }
  @PostMapping("/login") AuthResponse login(@Valid @RequestBody LoginRequest r) { return auth.login(r); }
  @PostMapping("/refresh") AuthResponse refresh(@Valid @RequestBody RefreshRequest r) { return auth.refresh(r); }
}
