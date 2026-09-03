package com.tcm_management_system.repository;

import com.tcm_management_system.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByVisitId(Long visitId);
    boolean existsByVisitId(Long visitId);
    List<Payment> findByPaymentDateBetweenOrderByPaymentDateAsc(LocalDate start, LocalDate end);
}