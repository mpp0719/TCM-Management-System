package com.tcm_management_system.repository;
import com.tcm_management_system.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientIdOrderByVisitDateDesc(Long patientId);
    List<Visit> findByNextAppointmentDateBetween(LocalDate start, LocalDate end);
    @Query("SELECT MAX(v.visitDate) FROM Visit v WHERE v.patient.id = :patientId")
    LocalDate findLastVisitDate(@Param("patientId") Long patientId);

    @Query("SELECT MIN(v.nextAppointmentDate) FROM Visit v WHERE v.patient.id = :patientId AND v.nextAppointmentDate >= CURRENT_DATE")
    LocalDate findNextUpcomingAppointment(@Param("patientId") Long patientId);

    @Query("SELECT v.patient.id, MAX(v.visitDate) FROM Visit v GROUP BY v.patient.id")
    List<Object[]> findLastVisitDatesByPatient();

    @Query("SELECT v.patient.id, MIN(v.nextAppointmentDate) FROM Visit v WHERE v.nextAppointmentDate >= CURRENT_DATE GROUP BY v.patient.id")
    List<Object[]> findNextUpcomingAppointmentsByPatient();
}