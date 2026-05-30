package com.aeropuerto.airport.domain.repository;

import com.aeropuerto.airport.domain.model.Payment;
import com.aeropuerto.airport.domain.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  @Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = :status")
  BigDecimal sumByStatus(PaymentStatus status);
  List<Payment> findAllByOrderByCreatedAtDesc();
}
