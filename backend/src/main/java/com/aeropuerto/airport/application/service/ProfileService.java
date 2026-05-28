package com.aeropuerto.airport.application.service;

import com.aeropuerto.airport.application.dto.ApiDtos.*;
import com.aeropuerto.airport.domain.repository.UserRepository;
import com.aeropuerto.airport.presentation.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
  private final UserRepository users; private final EntityMapper mapper;
  public ProfileService(UserRepository users, EntityMapper mapper) { this.users = users; this.mapper = mapper; }
  public ProfileResponse me(String email) {
    return mapper.toProfile(users.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
  }
  @Transactional public ProfileResponse update(String email, ProfileRequest r) {
    var u = users.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    u.setFullName(r.fullName()); u.setPhone(r.phone()); u.setDocumentId(r.documentId());
    return mapper.toProfile(u);
  }
}
