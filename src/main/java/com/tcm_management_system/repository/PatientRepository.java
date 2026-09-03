package com.tcm_management_system.repository;
import com.tcm_management_system.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByIcNo(String icNo);
    @Query("""
    SELECT p FROM Patient p
    WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :rawTerm, '%'))
       OR p.phoneNum LIKE CONCAT('%', :rawTerm, '%')
       OR (:icTerm <> '' AND p.icNo LIKE CONCAT('%', :icTerm, '%'))
    """)
    List<Patient> search(@Param("rawTerm") String rawTerm, @Param("icTerm") String icTerm);
}