package com.tcm_management_system.repository;

import com.tcm_management_system.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByName(String name);

    @Query("""
        SELECT COALESCE(SUM(r.restockQty), 0) - COALESCE(
            (SELECT SUM(vm.quantityDispensed) FROM VisitMedication vm WHERE vm.medicine.id = :medicineId), 0
        )
        FROM MedicineRestock r
        WHERE r.medicine.id = :medicineId
        """)
    Integer getCurrentStock(Long medicineId);
}