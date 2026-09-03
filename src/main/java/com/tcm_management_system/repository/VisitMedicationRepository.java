package com.tcm_management_system.repository;
import com.tcm_management_system.model.VisitMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitMedicationRepository extends JpaRepository<VisitMedication, Long> {
    List<VisitMedication> findByVisitId(Long visitId);
}